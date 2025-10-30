package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.UserData;
import model.*;
import io.javalin.*;

import io.javalin.http.Context;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.UnauthorizedException;
import service.SessionService;

import java.util.ArrayList;
import java.util.Map;

public class Server {

    private final Javalin server;
    private final SessionService sessionService;




    public Server() {
        UserDataAccess uDAO = new UserMemoryDataAccess();
        AuthDataAccess aDAO = new AuthMemoryDataAccess();
        GameDataAccess gDAO = new GameMemoryDataAccess();
        sessionService = new SessionService(uDAO, aDAO, gDAO);
        server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        
        server.delete("db", this::clear);
        server.post("user", this::register);
        server.post("session", this::login);
        server.delete("session", this::logout);
        server.post("game", this::createGame);
        server.put("game", this::joinGame);
        server.get("game", this::listGames);

    }
    
    private void register(Context ctx) {
        try{
            var serializer = new Gson();
            String reqJson = ctx.body();
            var user = serializer.fromJson(reqJson, UserData.class);

            var authData = sessionService.register(user);
            ctx.result(serializer.toJson(authData));



        } catch (BadRequestException ex){
            var msg = "{ \"message\": \"Error: bad request\" }";
            ctx.status(400).result(msg);

        } catch (AlreadyTakenException ex){
            var msg = "{ \"message\": \"Error: already taken\" }";
            ctx.status(403).result(msg);

        } catch (DataAccessException ex){
            var msg = "{ \"message\": \"Error: Data Access\" }";
            ctx.status(500).result(msg);
        }

    }


    private void login(Context ctx) {
        try{
            var serializer = new Gson();
            String reqJson = ctx.body();
            var user = serializer.fromJson(reqJson, UserData.class);

            var authData = sessionService.login(user);
            ctx.result(serializer.toJson(authData));



        } catch (UnauthorizedException ex){
            var msg = "{ \"message\": \"Error: unauthorized\" }";
            ctx.status(401).result(msg);

        }catch (BadRequestException ex){
            var msg = "{ \"message\": \"Error: bad request\" }";
            ctx.status(400).result(msg);

        } catch (DataAccessException ex){
            var msg = "{ \"message\": \"Error: Data Access\" }";
            ctx.status(500).result(msg);
        }

    }

    private void logout(Context ctx) {
        try{
            String authToken = ctx.header("authorization");
            sessionService.logout(authToken);

        } catch (UnauthorizedException ex){
            var msg = "{ \"message\": \"Error: unauthorized\" }";
            ctx.status(401).result(msg);

        }

    }

    private void createGame(Context ctx) {
        try{
            var serializer = new Gson();
            String reqJson = ctx.body();
            var game = serializer.fromJson(reqJson, GameData.class);
            String authToken = ctx.header("authorization");
            var gameID = sessionService.createGame(game, authToken);
            String result = "{ \"gameID\": " + gameID + "}";
            ctx.result(result);



        } catch (UnauthorizedException ex){
            var msg = "{ \"message\": \"Error: unauthorized\" }";
            ctx.status(401).result(msg);

        }catch (BadRequestException ex){
            var msg = "{ \"message\": \"Error: bad request\" }";
            ctx.status(400).result(msg);

        }
    }

    private void joinGame(Context ctx) {

        try{
            var serializer = new Gson();
            String reqJson = ctx.body();
            var body = serializer.fromJson(reqJson, Map.class);
            String authToken = ctx.header("authorization");

            Double idDouble = (Double) body.get("gameID");
            if(idDouble == null){
                throw new BadRequestException("Error: Bad request");
            }
            int iD = idDouble.intValue();
            String playerColor = (String) body.get("playerColor");
            if(playerColor == null || !playerColor.equals("WHITE") && !playerColor.equals("BLACK")){
                throw new BadRequestException("Error: Bad request");
            }
            sessionService.joinGame(iD, authToken, playerColor);



        } catch (UnauthorizedException ex){
            var msg = "{ \"message\": \"Error: unauthorized\" }";
            ctx.status(401).result(msg);

        }catch (BadRequestException ex){
            var msg = "{ \"message\": \"Error: bad request\" }";
            ctx.status(400).result(msg);

        } catch (AlreadyTakenException ex) {
            var msg = "{ \"message\": \"Error: already taken\" }";
            ctx.status(403).result(msg);

        }
    }
    private void listGames(Context ctx){
        try{
            String authToken = ctx.header("authorization");

            ArrayList<GameInfo> gamesList = sessionService.listGames(authToken);

            String result = new Gson().toJson(Map.of("games",gamesList));
            ctx.result(result);


        } catch (UnauthorizedException ex){
            var msg = "{ \"message\": \"Error: unauthorized\" }";
            ctx.status(401).result(msg);

        }
    }



    private void clear(Context ctx) {
        try{
            sessionService.clear();

        } catch (Exception e) {
            throw new RuntimeException(e);

        }
    }





    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
