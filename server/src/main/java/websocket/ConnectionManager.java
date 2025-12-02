package websocket;

import com.google.gson.Gson;
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

    public void broadcast(Session excludeSession, ServerMessage message, String msg) throws IOException {
        var serializer = new Gson();
        for (Session c : connections.values()) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(serializer.toJson(message));
                }
            }
        }
    }

    public void send(Session includedSession, ServerMessage message, String msg) throws IOException {
        if(includedSession.isOpen()){
            var serializer = new Gson();
//            includedSession.getRemote().sendString(msg);
            includedSession.getRemote().sendString(serializer.toJson(message));
        }
    }
}