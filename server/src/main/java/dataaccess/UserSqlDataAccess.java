package dataaccess;

import model.UserData;

public class UserSqlDataAccess implements UserDataAccess{

    public UserSqlDataAccess() throws DataAccessException {
        configureDatabase();
    }
    @Override
    public void clear() throws DataAccessException {
        executeCommand("DELETE from users");
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if(user.username() != null){
            var u = new UserData(user.username(), user.password(), user.email());
            executeUpdate("INSERT INTO 'users' (username, password, email) VALUES (?, ?, ?)", u.username(), u.password(), u.email());
        }

    }

    @Override
    public UserData getUser(String username) {
        return null;
    }


    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `username` varchar(25) PRIMARY KEY,
              `password` varchar(60) NOT NULL,
              `email` varchar(256) NOT NULL
            );
            """
    };

    private void executeUpdate(String statement, Object... params) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);

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
