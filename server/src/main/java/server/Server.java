package server;

import com.google.gson.Gson;
import dataModel.UserData;
import dataaccess.UserDataAccess;
import dataaccess.UserMemoryDataAccess;
import io.javalin.*;

import io.javalin.http.Context;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.UnauthorizedException;
import service.UserService;

public class Server {

    private final Javalin server;
    private final UserService userService;




    public Server() {
        UserDataAccess DAO = new UserMemoryDataAccess();
        userService = new UserService(DAO);
        server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        
        server.delete("db", ctx->ctx.result("{}"));
        server.post("user", this::register);
        server.post("session", this::login);

    }
    
    private void register(Context ctx) {
        try{
            var serializer = new Gson();
            String reqJson = ctx.body();
            var user = serializer.fromJson(reqJson, UserData.class);

            var authData = userService.register(user);
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

            var authData = userService.login(user);
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

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
