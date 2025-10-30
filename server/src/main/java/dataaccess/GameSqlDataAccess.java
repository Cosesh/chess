package dataaccess;

import model.GameData;
import model.GameInfo;

import java.util.ArrayList;

public class GameSqlDataAccess implements GameDataAccess{
    @Override
    public void clear() {

    }

    @Override
    public int createGame(GameData game) {
        return 0;
    }

    @Override
    public ArrayList<GameInfo> listGames() {
        return null;
    }

    @Override
    public void updateGame(int iD, String color, String user) {

    }

    @Override
    public GameData getGame(int iD) {
        return null;
    }
}
