package ui.websocket;



import model.GameData;

public interface NotificationHandler {
    void notify(String message);
    void load(GameData game);
    void error(String message);


}
