package websocket;

import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;


import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Session> connections = new ConcurrentHashMap<>();

    public void add(Session session, int gameID) {
        connections.put(gameID, session);
    }

    public void remove(int gameID) {
        connections.remove(gameID);
    }

    public void broadcast(Session excludeSession, ServerMessage message) throws IOException {
        String msg = "message";
        for (Session c : connections.values()) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}