package dataaccess;

import model.UserData;

public interface UserDataAccess {
    void clear();
    void createUser(UserData user);
    UserData getUser(String username);
}
