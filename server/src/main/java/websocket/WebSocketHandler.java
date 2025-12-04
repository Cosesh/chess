package websocket;

import chess.ChessBoard;
import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.websocket.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import server.Server;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private AuthDataAccess aDAO = new AuthSqlDataAccess();
    private GameDataAccess gDAO = new GameSqlDataAccess();

    public WebSocketHandler(AuthDataAccess aDAO, GameDataAccess gDAO) throws DataAccessException {
        this.aDAO = aDAO;
        this.gDAO = gDAO;
    }


    @Override
    public void handleClose(WsCloseContext wsCloseContext) {
        System.out.println("Websocket closed");

    }

    @Override
    public void handleConnect(WsConnectContext wsConnectContext)  {
        System.out.println("Websocket connected");
        wsConnectContext.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext wsMessageContext)  {
        int gameId = -1;
        Session session = wsMessageContext.session;


        try {
            var Serializer = new Gson();
            UserGameCommand command = Serializer.fromJson(
                    wsMessageContext.message(), UserGameCommand.class);

            if(command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE){
                MakeMoveCommand moveCommand = Serializer.fromJson(
                        wsMessageContext.message(), MakeMoveCommand.class);
                makeMove(session,moveCommand);
            }


            switch (command.getCommandType()) {
                case CONNECT -> connect(session, command);

            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }


    }


    private void saveSession(int gameId, Session session) {
        connections.add(session, gameId);
    }

    private void connect(Session session, UserGameCommand command) throws IOException, DataAccessException {
        var serializer = new Gson();
        var ident = command.getGameID();

        if(gDAO.getGame(ident) == null || aDAO.getAuth(command.getAuthToken()) == null) {
            ServerMessage servMessError = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,"Game ID does not exist");
            connections.send(session,servMessError);
            return;
        }
        String username = aDAO.getAuth(command.getAuthToken()).username();
        connections.add(session, ident);
        var game = gDAO.getGame(ident).game();
        ServerMessage servMessRoot = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,game);
        String msg = username + " joined as";
        ServerMessage servMessOther = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,msg);
        connections.broadcast(session,servMessOther, ident);
        connections.send(session, servMessRoot);
    }


    private void makeMove(Session session, MakeMoveCommand command) throws DataAccessException, InvalidMoveException, IOException {
        var ident = command.getGameID();
        var serializer = new Gson();

        if(aDAO.getAuth(command.getAuthToken()) == null) {
            ServerMessage servMessError = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,"That is so unauthorized");
            connections.send(session,servMessError);
            return;
        }
        String username = aDAO.getAuth(command.getAuthToken()).username();
        GameData updatedGameData = gDAO.getGame(ident);
        ChessGame updatedGame = updatedGameData.game();
        String whiteUser = updatedGameData.whiteUsername();
        String blackUser = updatedGameData.blackUsername();
        var turnColor = updatedGame.getTeamTurn();
        if(turnColor.equals(ChessGame.TeamColor.BLACK) )
        var moveToMake = command.getMove();
        var validMoves = updatedGame.validMoves(moveToMake.getStartPosition());
        if(validMoves.contains(moveToMake)){
            updatedGame.makeMove(command.getMove());

            gDAO.updateGameData(serializer.toJson(updatedGame),ident);
            ServerMessage servMessAll = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,updatedGame);
            connections.sendToAll(servMessAll,ident);
            String msg = username + "made this move: " + moveToMake;
            ServerMessage servMessOther = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,msg);
            connections.broadcast(session,servMessOther,ident);
        } else{
            ServerMessage servMessError = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,"That lowkey not valid");
            connections.send(session,servMessError);
            return;
        }


    }


}
