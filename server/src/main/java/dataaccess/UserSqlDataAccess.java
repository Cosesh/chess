package dataaccess;

import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Types.NULL;

/**
 * Ask about executeUpdate()
 * and getUser how to finish
 */

public class UserSqlDataAccess implements UserDataAccess {

    private final SqlHelper helper;

    public UserSqlDataAccess() throws DataAccessException {
        helper = new SqlHelper();
        helper.configureDatabase(createStatements);
    }
    @Override
    public void clear() throws DataAccessException {
        helper.executeCommand("DELETE from users");
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if(user.username() == null){throw new DataAccessException("Exception");}
            var u = new UserData(user.username(), user.password(), user.email());
            helper.executeUpdate("INSERT INTO users (username, password, email) VALUES (?, ?, ?)", u.username(), u.password(), u.email());

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        if(username == null){throw new DataAccessException("Exception");}
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("SELECT * from users WHERE username = ?")) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if(rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Data Access Exception");
        }
        return  null;
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username, password, email);
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
}
