package dataaccess;

import model.GameData;
import model.UserData;

import java.util.HashMap;

public class GameMemoryDataAccess implements GameDataAccess{
    private final HashMap<Integer, GameData> games = new HashMap<>();
    private int nextGameID = 0;

    @Override
    public void clear() {
        games.clear();
    }

    @Override
    public int createGame(GameData game) {
        nextGameID = nextGameID + 1;
        games.put(nextGameID, game);
        return nextGameID;
    }

    @Override
    public void listGames() {

    }

    @Override
    public void updateGame(int ID, String color, String user) {
        GameData old = games.get(ID);
        if(color.equals("WHITE")){
            GameData updated = new GameData(old.gameID(), user, old.blackUsername(), old.gameName(), old.game());
            games.put(ID,updated);
        } else if(color.equals("BLACK")){
            GameData updated = new GameData(old.gameID(), old.whiteUsername(), user, old.gameName(), old.game());
            games.put(ID,updated);
        }


    }

    @Override
    public GameData getGame(int ID) {
        return games.get(ID);
    }

}
