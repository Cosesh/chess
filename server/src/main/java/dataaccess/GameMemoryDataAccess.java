package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.GameInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class GameMemoryDataAccess implements GameDataAccess{
    private final HashMap<Integer, GameData> games = new HashMap<>();
    private int nextGameID = 0;

    @Override
    public void clear() {
        games.clear();
    }

    @Override
    public int createGame(String gameName) {
        nextGameID = nextGameID + 1;
        GameData add = new GameData(nextGameID, null, null, gameName ,new ChessGame());
        games.put(nextGameID, add);
        return nextGameID;
    }

    @Override
    public ArrayList<GameInfo> listGames() {
        ArrayList<GameInfo> gamesList = new ArrayList<>();
        for(GameData game: games.values()){
            GameInfo add = new GameInfo(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName());
            gamesList.add(add);
        }
        return gamesList;
    }

    @Override
    public void updateGame(int iD, String color, String user) {
        GameData old = games.get(iD);
        if(color.equals("WHITE")){
            GameData updated = new GameData(old.gameID(), user, old.blackUsername(), old.gameName(), old.game());
            games.put(iD,updated);
        } else if(color.equals("BLACK")){
            GameData updated = new GameData(old.gameID(), old.whiteUsername(), user, old.gameName(), old.game());
            games.put(iD,updated);
        }


    }

    @Override
    public GameData getGame(int iD) {
        return games.get(iD);
    }

}
