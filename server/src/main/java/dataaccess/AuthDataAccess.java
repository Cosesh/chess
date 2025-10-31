package dataaccess;

import model.AuthData;

import java.util.HashMap;

public interface AuthDataAccess {

    void clear() throws DataAccessException;
    void createAuth(AuthData auth) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    HashMap<String,AuthData> getMap();
}
