package dataaccess;

import model.*;

import java.util.ArrayList;
import java.util.Objects;

public interface GameDataAccess {
    void clear() throws DataAccessException;
    int createGame(String gameName) throws DataAccessException;
    ArrayList<GameInfo> listGames();
    void updateGame(int iD, String color, String user) throws DataAccessException;
    GameData getGame(int iD) throws DataAccessException;

}
