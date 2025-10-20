package service;

import dataaccess.GameDataAccess;
import model.AuthData;
import model.GameData;
import model.GameInfo;
import model.UserData;
import dataaccess.AuthDataAccess;
import dataaccess.UserDataAccess;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class SessionService {


    private final UserDataAccess UserDAO;
    private final AuthDataAccess AuthDAO;
    private final GameDataAccess GameDAO;

    public SessionService(UserDataAccess UserDAO, AuthDataAccess AuthDAO, GameDataAccess GameDAO ){
        this.UserDAO = UserDAO;
        this.AuthDAO = AuthDAO;
        this.GameDAO = GameDAO;
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

    public int createGame(GameData game, String auth) throws BadRequestException, UnauthorizedException {
        if(game.gameName() == null) {
            throw new BadRequestException("Error: Bad request");
        } else if(AuthDAO.getAuth(auth) == null){
            throw new UnauthorizedException("Error: unauthorized");
        } else {
            return GameDAO.createGame(game);

        }
    }

    public void joinGame(int gameID, String auth, String color) throws UnauthorizedException, BadRequestException, AlreadyTakenException {
        if(AuthDAO.getAuth(auth) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        } else if(gameID <= 0 || GameDAO.getGame(gameID) == null){
            throw new BadRequestException("Error: Bad request");
        } else if(color.equals("WHITE") && GameDAO.getGame(gameID).whiteUsername() != null){
            throw new AlreadyTakenException("Error: already taken");
        } else if(color.equals("BLACK") && GameDAO.getGame(gameID).blackUsername() != null) {
            throw new AlreadyTakenException("Error: already taken");
        } else {
            GameDAO.updateGame(gameID, color, AuthDAO.getAuth(auth).username());
        }
    }

    public ArrayList<GameInfo> listGames(String authToken) throws UnauthorizedException {
        if(AuthDAO.getAuth(authToken) == null){
            throw new UnauthorizedException("Error: unauthorized");
        } else {
            return GameDAO.listGames();
        }
    }

    public void clear() {
        AuthDAO.clear();
        UserDAO.clear();
        GameDAO.clear();
    }


}
