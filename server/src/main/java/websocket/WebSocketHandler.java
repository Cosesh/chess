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
import java.util.Objects;

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
                case LEAVE -> leave(session, command);
                case RESIGN -> resign(session, command);

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
            ServerMessage servMessError = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Game ID does not exist\n");
            connections.send(session,servMessError);
            return;
        }
        String username = aDAO.getAuth(command.getAuthToken()).username();
        connections.add(session, ident);
        var gameData = gDAO.getGame(ident);
        var game = gameData.game();
        ServerMessage servMessRoot = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,game);
        String msg = "";
        if(gameData.whiteUsername().equals(username)){
            msg = username + " joined as white\n";
        } if(gameData.blackUsername().equals(username)){
            msg = username + " joined as black\n";
        } if(!gameData.whiteUsername().equals(username) && !gameData.blackUsername().equals(username))
        {
            msg = username + " joined as observer\n";
        }

        ServerMessage servMessOther = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,msg);
        connections.broadcast(session,servMessOther, ident);
        connections.send(session, servMessRoot);
    }


    private void makeMove(Session session, MakeMoveCommand command) throws DataAccessException, InvalidMoveException, IOException {
        var ident = command.getGameID();
        var serializer = new Gson();

        if(aDAO.getAuth(command.getAuthToken()) == null) {
            ServerMessage servMessError = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "That is so unauthorized\n");
            connections.send(session,servMessError);
            return;
        }

        String username = aDAO.getAuth(command.getAuthToken()).username();
        GameData updatedGameData = gDAO.getGame(ident);
        ChessGame updatedGame = updatedGameData.game();
        if(updatedGame.isFinished()){
            ServerMessage servMessError = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Game is over. Cannot make move\n");
            connections.send(session,servMessError);
            return;
        }
        String whiteUser = updatedGameData.whiteUsername();
        String blackUser = updatedGameData.blackUsername();
        var turnColor = updatedGame.getTeamTurn();



        if(turnColor.equals(ChessGame.TeamColor.WHITE) && !username.equals(whiteUser)){
            ServerMessage servMessError = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Black cannot move. it is white's turn\n");
            connections.send(session,servMessError);
            return;
        }

        if(turnColor.equals(ChessGame.TeamColor.BLACK) && !username.equals(blackUser)){
            ServerMessage servMessError = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "White cannot move. it is black's turn\n");
            connections.send(session,servMessError);
            return;
        }

        var moveToMake = command.getMove();
        var validMoves = updatedGame.validMoves(moveToMake.getStartPosition());

        if(validMoves.contains(moveToMake)){
            updatedGame.makeMove(command.getMove());

            gDAO.updateGameData(serializer.toJson(updatedGame),ident);
            ServerMessage servMessAll = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,updatedGame);
            connections.sendToAll(servMessAll,ident);
            String msg = username + " made a move: " + moveToMake + "\n";
            if(updatedGame.isInCheck(ChessGame.TeamColor.WHITE)){
                msg += "you are in check do something quick \n";
            } if(updatedGame.isInCheck(ChessGame.TeamColor.BLACK)){
                msg += "you are in check please don't lose :((((( \n";
            }
            ServerMessage servMessOther = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,msg);
            connections.broadcast(session,servMessOther,ident);

            if(isCheckmate(updatedGame)){
                servMessAll = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        "checkmate\n");
                connections.sendToAll(servMessAll,ident);
                updatedGame.setFinished(true);
                gDAO.updateGameData(serializer.toJson(updatedGame),ident);

            } if(isStalemate(updatedGame)){
                servMessAll = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        "stalemate\n");
                connections.sendToAll(servMessAll,ident);
                updatedGame.setFinished(true);
                gDAO.updateGameData(serializer.toJson(updatedGame),ident);

            }
        } else{
            ServerMessage servMessError = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "That lowkey not valid");
            connections.send(session,servMessError);
        }


    }

    private void leave(Session session, UserGameCommand command) throws DataAccessException, IOException {
        var ident = command.getGameID();
        var serializer = new Gson();
        String username = aDAO.getAuth(command.getAuthToken()).username();
        GameData myGame = gDAO.getGame(ident);
        if(Objects.equals(myGame.whiteUsername(), username)) {
            gDAO.removeUser(ident,"WHITE");
            ServerMessage servMessOther = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,username + " has left the game");
            connections.broadcast(session,servMessOther,ident);
            connections.remove(ident,session);
        }
        if(Objects.equals(myGame.blackUsername(), username)) {
            gDAO.removeUser(ident,"BLACK");
            ServerMessage servMessOther = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,username +" has left the game");
            connections.broadcast(session,servMessOther,ident);
            connections.remove(ident,session);
        } if(!Objects.equals(myGame.whiteUsername(), username)
                && !Objects.equals(myGame.blackUsername(), username)){
            ServerMessage servMessOther = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,username + " has stopped observing");
            connections.broadcast(session,servMessOther,ident);
            connections.remove(ident,session);
        }


    }

    private void resign(Session session, UserGameCommand command) throws DataAccessException, IOException {
        var ident = command.getGameID();
        var serializer = new Gson();
        String username = aDAO.getAuth(command.getAuthToken()).username();
        GameData myGameData = gDAO.getGame(ident);
        ChessGame myGame = myGameData.game();
        if(!myGameData.whiteUsername().equals(username)
                && !myGameData.blackUsername().equals(username)) {
            ServerMessage servMessError = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Observer cannot resign. He aint even playin");
            connections.send(session, servMessError);
            return;
        }

        if(myGame.isFinished()) {
            ServerMessage servMessError = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Cannot resign. The game is already over twin");
            connections.send(session, servMessError);
            return;
        }
        myGame.setFinished(true);
        ServerMessage servMessAll = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                username + " has resigned because they are scared");
        connections.sendToAll(servMessAll,ident);
        gDAO.updateGameData(serializer.toJson(myGame),ident);


    }

    private static boolean isCheckmate(ChessGame updatedGame) {

        return(updatedGame.isInCheckmate(ChessGame.TeamColor.WHITE) ||
                updatedGame.isInCheckmate(ChessGame.TeamColor.BLACK));

    }

    private static boolean isStalemate(ChessGame updatedGame) {

        return(updatedGame.isInStalemate(ChessGame.TeamColor.WHITE) ||
                updatedGame.isInStalemate(ChessGame.TeamColor.BLACK));

    }
}


