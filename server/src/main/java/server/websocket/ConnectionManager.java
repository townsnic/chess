package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void addConnection(String authToken, Session session) {
        Connection connection = new Connection(authToken, session);
        connections.put(authToken, connection);
    }

    public void removeConnection(String authToken) {
        connections.remove(authToken);
    }

    public Connection getConnection(String authToken, Session session) {
        return connections.get(authToken);
    }

    public void broadcast(String excludeAuth, ServerMessage message) throws IOException {
        ArrayList<Connection> removeList = new ArrayList<Connection>();
        for (Connection con : connections.values()) {
            if (con.session.isOpen()) {
                if (!con.authToken.equals(excludeAuth)) {
                    System.out.println(message);
                    String json = new Gson().toJson(message);
                    con.session.getRemote().sendString(json);
                }
            } else {
                removeList.add(con);
            }
        }

        for (Connection con : removeList) {
            connections.remove(con.authToken);
        }
    }

    public void broadcastLoadGame(String excludeAuth, LoadGameMessage message) throws IOException {
        ArrayList<Connection> removeList = new ArrayList<Connection>();
        for (Connection con : connections.values()) {
            if (con.session.isOpen()) {
                String json = new Gson().toJson(message);
                con.session.getRemote().sendString(json);
            } else {
                removeList.add(con);
            }
        }

        for (Connection con : removeList) {
            connections.remove(con.authToken);
        }
    }

    public void sendToSelf(String excludeAuth, ServerMessage message) throws IOException {
        ArrayList<Connection> removeList = new ArrayList<Connection>();
        for (Connection con : connections.values()) {
            if (con.session.isOpen()) {
                if (con.authToken.equals(excludeAuth)) {
                    System.out.println(message);
                    String json = new Gson().toJson(message);
                    con.session.getRemote().sendString(json);
                }
            } else {
                removeList.add(con);
            }
        }

        for (Connection con : removeList) {
            connections.remove(con.authToken);
        }
    }
}