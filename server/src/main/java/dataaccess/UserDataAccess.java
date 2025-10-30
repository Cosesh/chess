package dataaccess;

import model.UserData;

public interface UserDataAccess {
    void clear() throws DataAccessException;
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username);
}
