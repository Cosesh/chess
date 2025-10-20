package dataaccess;

import model.*;

public interface GameDataAccess {
    void clear();
    int createGame(GameData game);
    void listGames();
    void updateGame(int ID, String color, String user);
    GameData getGame(int ID);
}
