package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
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
                    con.send(message.toString());
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