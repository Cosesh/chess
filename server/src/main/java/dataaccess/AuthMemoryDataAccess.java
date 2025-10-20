package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class AuthMemoryDataAccess implements AuthDataAccess{
    private final HashMap<String, AuthData> auths = new HashMap<>();

    @Override
    public void clear() {
        auths.clear();
    }

    @Override
    public void createAuth(AuthData auth) {
        auths.put(auth.authToken(), auth);
    }

    @Override
    public void deleteAuth(String auth) {
        auths.remove(auth);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return auths.get(authToken);
    }


}
