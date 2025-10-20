package dataaccess;

import model.*;

import java.util.ArrayList;
import java.util.Objects;

public interface GameDataAccess {
    void clear();
    int createGame(GameData game);
    ArrayList<GameInfo> listGames();
    void updateGame(int ID, String color, String user);
    GameData getGame(int ID);

}
