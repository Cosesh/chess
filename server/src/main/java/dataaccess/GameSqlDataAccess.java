package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.GameInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static java.sql.Types.NULL;

public class GameSqlDataAccess implements GameDataAccess{
    public GameSqlDataAccess() throws DataAccessException {
        configureDatabase();
    }
    @Override
    public void clear() throws DataAccessException {
        executeCommand("DELETE from games");
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        ChessGame game = new ChessGame();
        executeUpdate("INSERT INTO games (gameName, game) " +
                        "VALUES (?, ?)",
                gameName,
                game.toString());
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("SELECT gameID from games WHERE gameName = ?")) {
                ps.setString(1, gameName);
                try (ResultSet rs = ps.executeQuery()) {
                    if(rs.next()) {
                        return rs.getInt("gameID");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;

    }

    @Override
    public ArrayList<GameInfo> listGames() {
        return null;
    }

    @Override
    public void updateGame(int iD, String color, String user) throws DataAccessException {
        if(color.equals("WHITE")){
            executeUpdate("UPDATE games SET whiteUsername = ? WHERE gameID = ? ", user, iD);

        } else if(color.equals("BLACK")){
            executeUpdate("UPDATE games SET blackUsername = ? WHERE gameID = ? ", user, iD);
        }

    }

    @Override
    public GameData getGame(int iD) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("SELECT * from games WHERE gameID = ?")) {
                ps.setInt(1, iD);
                try (ResultSet rs = ps.executeQuery()) {
                    if(rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return  null;
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var gs = rs.getString("game");
        var gameID = rs.getInt("gameID");
        var gameName =rs.getString("gameName");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var game = chess.ChessGame.fromString(gs);
        return new GameData(gameID,whiteUsername,blackUsername,gameName, game);

    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    if (param instanceof Integer p) ps.setInt(i + 1, p);
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
            CREATE TABLE IF NOT EXISTS  games (
              `gameID` int PRIMARY KEY AUTO_INCREMENT,
              `whiteUsername` varchar(25),
              `blackUsername` varchar(25),
              `gameName` varchar(255) NOT NULL,
              `game` text
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
