package service;

import dataaccess.GameDataAccess;
import model.AuthData;
import model.GameData;
import model.GameInfo;
import model.UserData;
import dataaccess.AuthDataAccess;
import dataaccess.UserDataAccess;

import java.util.ArrayList;
import java.util.UUID;

public class SessionService {



    private final UserDataAccess userDAO;
    private final AuthDataAccess authDAO;
    private final GameDataAccess gameDAO;

    public SessionService(UserDataAccess userDAO, AuthDataAccess authDAO, GameDataAccess gameDAO ){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData register(UserData user) throws AlreadyTakenException, BadRequestException {

        if(userDAO.getUser(user.username()) != null){
            throw new AlreadyTakenException("Error: already taken");
        }else if(user.password() == null){
            throw new BadRequestException("Error: bad request");
        }else {
            AuthData auth = new AuthData(generateToken(), user.username());
            userDAO.createUser(user);
            authDAO.createAuth(auth);
            return auth;
        }

    }

    public AuthData login(UserData user) throws UnauthorizedException, BadRequestException {
        if(user.username() == null || user.password() == null){
            throw new BadRequestException("Error: bad request");
        }
        else if(userDAO.getUser(user.username()) == null){
            throw new UnauthorizedException("Error: unauthorized");
        } else if (!userDAO.getUser(user.username()).password().equals(user.password())) {
            throw new UnauthorizedException("Error: unauthorized");
        } else {
            AuthData auth = new AuthData(generateToken(), user.username());
            authDAO.createAuth(auth);
            return auth;
        }
    }

    public void logout(String auth) throws UnauthorizedException {

        var test = authDAO.getAuth(auth);
        if(authDAO.getAuth(auth) == null){
            throw new UnauthorizedException("Error: unauthorized");
        } else {
            authDAO.deleteAuth(auth);
        }

    }

    public int createGame(GameData game, String auth) throws BadRequestException, UnauthorizedException {
        if(game.gameName() == null) {
            throw new BadRequestException("Error: Bad request");
        } else if(authDAO.getAuth(auth) == null){
            throw new UnauthorizedException("Error: unauthorized");
        } else {
            return gameDAO.createGame(game);

        }
    }

    public void joinGame(int gameID, String auth, String color) throws UnauthorizedException, BadRequestException, AlreadyTakenException {
        if(authDAO.getAuth(auth) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        } else if(gameID <= 0 || gameDAO.getGame(gameID) == null){
            throw new BadRequestException("Error: Bad request");
        } else if(color.equals("WHITE") && gameDAO.getGame(gameID).whiteUsername() != null){
            throw new AlreadyTakenException("Error: already taken");
        } else if(color.equals("BLACK") && gameDAO.getGame(gameID).blackUsername() != null) {
            throw new AlreadyTakenException("Error: already taken");
        } else {
            gameDAO.updateGame(gameID, color, authDAO.getAuth(auth).username());
        }
    }

    public ArrayList<GameInfo> listGames(String authToken) throws UnauthorizedException {
        if(authDAO.getAuth(authToken) == null){
            throw new UnauthorizedException("Error: unauthorized");
        } else {
            return gameDAO.listGames();
        }
    }

    public void clear() {
        authDAO.clear();
        userDAO.clear();
        gameDAO.clear();
    }









}
