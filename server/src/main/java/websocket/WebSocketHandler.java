package websocket;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;
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
            var database = new AuthSqlDataAccess();
            UserGameCommand command = Serializer.fromJson(
                    wsMessageContext.message(), UserGameCommand.class);
            gameId = command.getGameID();
            String username = database.getAuth(command.getAuthToken()).username();

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, command);

            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }


    }

    private void saveSession(int gameId, Session session) {
        connections.add(session, gameId);
    }

    private void connect(Session session, String username, UserGameCommand command) throws IOException, DataAccessException {
        var serializer = new Gson();
        var ident = command.getGameID();
        connections.add(session, ident);
        var game = gDAO.getGame(ident).game();
        ServerMessage servMessRoot = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,game);
        String msg = username + " joined as";
        ServerMessage servMessOther = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,msg);
        connections.broadcast(session,servMessOther, ident);
        connections.send(session, servMessRoot);


    }


}
