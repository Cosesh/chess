package websocket;

import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ArrayList<Session>> connections = new ConcurrentHashMap<>();

    public void add(Session session, int gameID) {
        connections.computeIfAbsent(gameID, k -> new ArrayList<>());
        connections.get(gameID).add(session);
    }

    public void remove(int gameID, Session session) {
        connections.get(gameID).remove(session);
    }

    public void broadcast(Session excludeSession, ServerMessage message, int gameID) throws IOException {
        var serializer = new Gson();

        var game = connections.get(gameID);
        for(Session c: game){
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(serializer.toJson(message));
                }
            }
        }
    }

    public void send(Session includedSession, ServerMessage message) throws IOException {
        if(includedSession.isOpen()){
            var serializer = new Gson();
            includedSession.getRemote().sendString(serializer.toJson(message));
        }
    }
}