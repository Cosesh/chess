package ui.websocket;

import chess.ChessMove;
import ui.*;
import jakarta.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import com.google.gson.Gson;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;




public class WebSocketFacade extends Endpoint {

    private Session session;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    var serializer = new Gson();
                    ServerMessage incomingMessage = serializer.fromJson(message, ServerMessage.class);
                    switch (incomingMessage.getServerMessageType()){
                        case NOTIFICATION -> {
                            NotificationMessage notificationMessage = serializer.fromJson(message, NotificationMessage.class);
                            notificationHandler.notify(notificationMessage.getMessage());
                        }
                        case LOAD_GAME -> {
                            LoadGameMessage loadGameMessage = serializer.fromJson(message, LoadGameMessage.class);
                            notificationHandler.load(loadGameMessage.getGame());
                        }
                        case ERROR -> {
                            ErrorMessage errorMessage = serializer.fromJson(message, ErrorMessage.class);
                            notificationHandler.error(errorMessage.getErrorMessage());
                        }
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String auth, int ident) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT,auth, ident);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void leave(String auth, int ident) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.LEAVE,auth, ident);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void resign(String auth, int ident) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.RESIGN,auth, ident);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void makeMove(String auth, int ident, ChessMove move) throws ResponseException {
        try {
            var command = new MakeMoveCommand(auth, ident, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

}
