package service;

import dataModel.AuthData;
import dataModel.UserData;
import dataaccess.UserDataAccess;

public class UserService {


    private final UserDataAccess DAO;
    public  UserService(UserDataAccess DAO){
        this.DAO = DAO;
    }

    public AuthData register(UserData user) throws AlreadyTakenException, BadRequestException {

        if(DAO.getUser(user.username()) != null){
            throw new AlreadyTakenException("already taken");
        }else if(user.password() == null){
            throw new BadRequestException("Error: bad request");
        }else {
            AuthData auth = new AuthData("xyz", user.username());
            DAO.createUser(user);
            return auth;
        }

    }

    public AuthData login(UserData user) throws UnauthorizedException, BadRequestException {
        if(user.username() == null || user.password() == null){
            throw new BadRequestException("Error: bad request");
        }
        else if(DAO.getUser(user.username()) == null){
            throw new UnauthorizedException("Error: unauthorized");
        } else if (!DAO.getUser(user.username()).password().equals(user.password())) {
            throw new UnauthorizedException("Error: unauthorized");
        } else {
            AuthData auth = new AuthData("xyz", user.username());
            DAO.createUser(user);
            return auth;
        }
    }
}
