package ui.websocket;


import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void notify(ServerMessage message);
    void load(ServerMessage message);
    void error(ServerMessage message);
}
