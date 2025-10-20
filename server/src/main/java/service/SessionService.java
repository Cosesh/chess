package service;

import model.AuthData;
import model.UserData;
import dataaccess.AuthDataAccess;
import dataaccess.UserDataAccess;

import java.util.UUID;

public class SessionService {


    private final UserDataAccess UserDAO;
    private final AuthDataAccess AuthDAO;
    public SessionService(UserDataAccess UserDAO, AuthDataAccess AuthDAO){
        this.UserDAO = UserDAO;
        this.AuthDAO = AuthDAO;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData register(UserData user) throws AlreadyTakenException, BadRequestException {

        if(UserDAO.getUser(user.username()) != null){
            throw new AlreadyTakenException("Error: already taken");
        }else if(user.password() == null){
            throw new BadRequestException("Error: bad request");
        }else {
            AuthData auth = new AuthData(generateToken(), user.username());
            UserDAO.createUser(user);
            AuthDAO.createAuth(auth);
            return auth;
        }

    }

    public AuthData login(UserData user) throws UnauthorizedException, BadRequestException {
        if(user.username() == null || user.password() == null){
            throw new BadRequestException("Error: bad request");
        }
        else if(UserDAO.getUser(user.username()) == null){
            throw new UnauthorizedException("Error: unauthorized");
        } else if (!UserDAO.getUser(user.username()).password().equals(user.password())) {
            throw new UnauthorizedException("Error: unauthorized");
        } else {
            AuthData auth = new AuthData(generateToken(), user.username());
            AuthDAO.createAuth(auth);
            return auth;
        }
    }

    public void logout(String auth) throws UnauthorizedException {

        var test = AuthDAO.getAuth(auth);
        if(AuthDAO.getAuth(auth) == null){
            throw new UnauthorizedException("Error: unauthorized");
        } else {
            AuthDAO.deleteAuth(auth);
        }

    }

    public void clear() {
        AuthDAO.clear();
        UserDAO.clear();
    }


}
