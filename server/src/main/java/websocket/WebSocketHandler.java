package websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {

    }

    @Override
    public void handleConnect(@NotNull WsConnectContext wsConnectContext) throws Exception {
        System.out.println("Websocket connected");
        wsConnectContext.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext wsMessageContext) throws Exception {
//        int gameId = -1;
//        Session session = wsMessageContext.session;
//
//        try {
//            var Serializer = new Gson();
//            UserGameCommand command = Serializer.fromJson(
//                    wsMessageContext.message(), UserGameCommand.class);
//            gameId = command.getGameID();
//            String username = getUsername(command.getAuthToken());
//            saveSession(gameId, session);
//
//            switch (command.getCommandType()) {
//                case CONNECT -> connect(session, username, (UserGameCommand) command);
//
//            }
//        } catch (Exception ex) {
//            throw new Exception("exception");
//        }


    }

}
