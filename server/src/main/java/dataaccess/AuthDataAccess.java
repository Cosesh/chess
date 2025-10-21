package dataaccess;

import model.AuthData;

import java.util.HashMap;

public interface AuthDataAccess {

    void clear();
    void createAuth(AuthData auth);
    void deleteAuth(String authToken);
    AuthData getAuth(String authToken);
    HashMap<String,AuthData> getMap();
}
