package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.GameInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;



public class GameSqlDataAccess implements GameDataAccess{

    private final SqlHelper helper;

    public GameSqlDataAccess() throws DataAccessException {
        helper = new SqlHelper();
        String[] createStatements = {
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
        helper.configureDatabase(createStatements);
    }
    @Override
    public void clear() throws DataAccessException {
        helper.executeCommand("DELETE from games");
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        if(gameName == null){throw new DataAccessException("Data");}
        ChessGame game = new ChessGame();
        helper.executeUpdate("INSERT INTO games (gameName, game) " +
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
        } catch (Exception e) {
            throw new DataAccessException("Data Access Exception");
        }
        return 0;

    }

    @Override
    public ArrayList<GameInfo> listGames() throws DataAccessException {
        ArrayList<GameInfo> gamesList = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("SELECT * FROM games")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while(rs.next()) {
                        var game = readGame(rs);
                        GameInfo info = new GameInfo(game.gameID(),game.whiteUsername(),
                                game.blackUsername(),game.gameName());
                        gamesList.add(info);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Data Access Exception");
        }

        return gamesList;
    }

    @Override
    public void updateGame(int iD, String color, String user) throws DataAccessException {
        if(user == null){throw new DataAccessException("Data");}
        if(color.equals("WHITE")){
            helper.executeUpdate("UPDATE games SET whiteUsername = ? WHERE gameID = ? ", user, iD);

        } else if(color.equals("BLACK")){
            helper.executeUpdate("UPDATE games SET blackUsername = ? WHERE gameID = ? ", user, iD);
        }

    }

    public void removeUser(int iD, String color) throws DataAccessException {
        if(color.equals("WHITE")){
            helper.executeUpdate("UPDATE games SET whiteUsername = ? WHERE gameID = ? ", null, iD);

        } else if(color.equals("BLACK")){
            helper.executeUpdate("UPDATE games SET blackUsername = ? WHERE gameID = ? ", null, iD);
        }
    }

    public void updateGameData(String game,int iD) throws DataAccessException {
        if(game == null){
            throw new DataAccessException("Data");
        }
        helper.executeUpdate("UPDATE games SET game = ? WHERE gameID = ? ", game, iD);

    }

    @Override
    public GameData getGame(int iD) throws DataAccessException {
        if(iD < 0){throw new DataAccessException("Data");}
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("SELECT * from games WHERE gameID = ?")) {
                ps.setInt(1, iD);
                try (ResultSet rs = ps.executeQuery()) {
                    if(rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Data Access Exception");
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


}
