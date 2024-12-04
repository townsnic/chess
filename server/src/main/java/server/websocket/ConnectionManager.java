package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    public final ConcurrentHashMap<Integer, ConcurrentHashMap<String, Connection>> connections = new ConcurrentHashMap<>();

    public void addConnection(int gameID, String authToken, Session session) {
        Connection connection = new Connection(authToken, session);
        connections.computeIfAbsent(gameID, k -> new ConcurrentHashMap<>());
        connections.get(gameID).put(authToken, connection);
    }

    public void removeConnection(int gameID, String authToken) {
        connections.get(gameID).remove(authToken);
    }

    public void broadcast(int gameID, String excludeAuth, ServerMessage message) throws IOException {
        ArrayList<Connection> removeList = new ArrayList<Connection>();
        for (Connection con : connections.get(gameID).values()) {
            if (con.session.isOpen()) {
                if (!con.authToken.equals(excludeAuth)) {
                    System.out.println(message);
                    String json = new Gson().toJson(message);
                    con.send(json);
                }
            } else {
                removeList.add(con);
            }
        }

        for (Connection con : removeList) {
            connections.get(gameID).remove(con.authToken);
        }
    }

    public void sendToSelf(int gameID, String sendAuth, ServerMessage message) throws IOException {
        String json = new Gson().toJson(message);
        connections.get(gameID).get(sendAuth).send(json);
    }
}