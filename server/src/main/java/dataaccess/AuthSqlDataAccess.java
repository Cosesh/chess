package dataaccess;

import model.AuthData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static java.sql.Types.NULL;

public class AuthSqlDataAccess implements AuthDataAccess{

    public AuthSqlDataAccess() throws DataAccessException {
        configureDatabase();
    }
    @Override
    public void clear() throws DataAccessException {
        executeCommand("DELETE from auths");
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        if(auth.authToken() != null) {
            var a = new AuthData(auth.authToken(), auth.username());
            executeUpdate("INSERT INTO auths (authToken, username) VALUES (?, ?)", a.authToken(), a.username());
        }

    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if(authToken != null) {
            executeUpdate("DELETE FROM auths WHERE authToken = ?", authToken);
        }
    }

    @Override
    public AuthData getAuth(String authToken) {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("SELECT * from auths WHERE authToken = ?")) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if(rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
        return  null;
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var authToken = rs.getString("authToken");
        var username = rs.getString("username");
        return new AuthData(authToken, username);
    }

    @Override
    public HashMap<String, AuthData> getMap() {
        return null;
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);

                }
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: Data Access Exception");
        }
    }

    private void executeCommand(String statement) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: Data Access Exception");
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auths (
              `authToken` varchar(255) PRIMARY KEY,
              `username` varchar(25) NOT NULL
            );
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: Data Access Exception");
        }
    }


}
