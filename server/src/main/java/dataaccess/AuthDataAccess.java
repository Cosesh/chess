package dataaccess;

import model.AuthData;

public interface AuthDataAccess {

    void clear();
    void createAuth(AuthData auth);
    void deleteAuth(String authToken);
    AuthData getAuth(String authToken);
}
