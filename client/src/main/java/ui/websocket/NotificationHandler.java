package ui.websocket;


import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void notify(NotificationMessage message);
    void load(ServerMessage message);
    void error(ErrorMessage message);


}
