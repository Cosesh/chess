package dataaccess;

import model.AuthData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;


public class AuthSqlDataAccess implements AuthDataAccess{

    private final SqlHelper helper;
    public AuthSqlDataAccess() throws DataAccessException {
        helper = new SqlHelper();
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS  auths (
              `authToken` varchar(255) PRIMARY KEY,
              `username` varchar(25) NOT NULL
            );
            """
        };
        helper.configureDatabase(createStatements);
    }
    @Override
    public void clear() throws DataAccessException {
        helper.executeCommand("DELETE from auths");
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {

        var a = new AuthData(auth.authToken(), auth.username());
        helper.executeUpdate("INSERT INTO auths (authToken, username) VALUES (?, ?)",
                a.authToken(), a.username());

    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if(authToken == null){
            throw new DataAccessException("Data Exception");
        }
        helper.executeUpdate("DELETE FROM auths WHERE authToken = ?", authToken);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if(authToken == null) {
            throw new DataAccessException("Data Access Exception");
        }
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("SELECT * from auths WHERE authToken = ?")) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if(rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Data Access Exception");
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

}
