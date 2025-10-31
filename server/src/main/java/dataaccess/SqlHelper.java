package dataaccess;

import static java.sql.Types.NULL;

public class SqlHelper {

    public void executeUpdate(String statement, Object... params) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) {ps.setString(i + 1, p);}
                    if (param instanceof Integer p) {ps.setInt(i + 1, p);}
                    else if (param == null) {ps.setNull(i + 1, NULL);}

                }
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: Data Access Exception");
        }
    }

    public void executeCommand(String statement) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: Data Access Exception");
        }
    }

    public void configureDatabase(String[] createStatements) throws DataAccessException {
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
