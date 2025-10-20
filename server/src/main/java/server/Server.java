package server;

import com.google.gson.Gson;
import model.UserData;
import dataaccess.AuthDataAccess;
import dataaccess.AuthMemoryDataAccess;
import dataaccess.UserDataAccess;
import dataaccess.UserMemoryDataAccess;
import io.javalin.*;

import io.javalin.http.Context;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.UnauthorizedException;
import service.SessionService;

public class Server {

    private final Javalin server;
    private final SessionService sessionService;




    public Server() {
        UserDataAccess UDAO = new UserMemoryDataAccess();
        AuthDataAccess ADAO = new AuthMemoryDataAccess();
        sessionService = new SessionService(UDAO, ADAO);
        server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        
        server.delete("db", ctx->ctx.result("{}"));
        server.post("user", this::register);
        server.post("session", this::login);
        server.delete("session", this::logout);

    }
    
    private void register(Context ctx) {
        try{
            var serializer = new Gson();
            String reqJson = ctx.body();
            var user = serializer.fromJson(reqJson, UserData.class);

            var authData = sessionService.register(user);
            ctx.result(serializer.toJson(authData));



        } catch (BadRequestException ex){
            var msg = String.format("{ \"message\": \"Error: bad request\" }");
            ctx.status(400).result(msg);

        } catch (AlreadyTakenException ex){
            var msg = String.format("{ \"message\": \"Error: already taken\" }");
            ctx.status(403).result(msg);

        } catch (Exception ex){
            throw ex;
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
            var msg = String.format("{ \"message\": \"Error: unauthorized\" }");
            ctx.status(401).result(msg);

        }catch (BadRequestException ex){
            var msg = String.format("{ \"message\": \"Error: bad request\" }");
            ctx.status(400).result(msg);

        } catch (Exception ex){
            throw ex;
        }

    }

    private void logout(Context ctx) {
        try{
            String authToken = ctx.header("authorization");
            sessionService.logout(authToken);

        } catch (UnauthorizedException ex){
            var msg = String.format("{ \"message\": \"Error: unauthorized\" }");
            ctx.status(401).result(msg);

        } catch (Exception ex){
            throw ex;
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
