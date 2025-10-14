package dataaccess;

import dataModel.UserData;

import java.util.HashMap;

public class UserMemoryDataAccess implements UserDataAccess {
    private final HashMap<String, UserData> users = new HashMap<>();
    @Override
    public void clear() {
        users.clear();
    }

    @Override
    public void createUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }
}
