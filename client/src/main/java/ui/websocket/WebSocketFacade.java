package ui.websocket;

import jakarta.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import com.google.gson.Gson;
import websocket.messages.ServerMessage;



public class WebSocketFacade extends Endpoint {

    private Session session;
    private NotificationHandler notificationHandler;
    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage incomingMessage = new Gson().fromJson(message, ServerMessage.class);
                    switch (incomingMessage.getServerMessageType()){
                        case NOTIFICATION -> notificationHandler.notify(incomingMessage);
                        case LOAD_GAME -> notificationHandler.load(incomingMessage);
                        case ERROR -> notificationHandler.error(incomingMessage);
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }





}
