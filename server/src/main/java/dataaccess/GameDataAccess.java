package dataaccess;

import model.*;

import java.util.ArrayList;

public interface GameDataAccess {
    void clear() throws DataAccessException;
    int createGame(String gameName) throws DataAccessException;
    ArrayList<GameData> listGames() throws DataAccessException;
    void updateGame(int iD, String color, String user) throws DataAccessException;
    GameData getGame(int iD) throws DataAccessException;
    void updateGameData(String game, int iD) throws DataAccessException;
    void removeUser(int iD, String Color) throws DataAccessException;
}
