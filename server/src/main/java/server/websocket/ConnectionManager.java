package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ConcurrentHashMap<String, Connection>> connections = new ConcurrentHashMap<>();
    //public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void addConnection(int gameID, String authToken, Session session) {
        Connection connection = new Connection(authToken, session);
        connections.computeIfAbsent(gameID, k -> new ConcurrentHashMap<>());
        connections.get(gameID).put(authToken, connection);
    }

    public void removeConnection(int gameID, String authToken) {
        connections.get(gameID).remove(authToken);
    }

    public Connection getConnection(int gameID, String authToken, Session session) {
        return connections.get(gameID).get(authToken);
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

//    public void broadcastLoadGame(String excludeAuth, LoadGameMessage message) throws IOException {
//        ArrayList<Connection> removeList = new ArrayList<Connection>();
//        for (Connection con : connections.values()) {
//            if (con.session.isOpen()) {
//                String json = new Gson().toJson(message);
//                con.send(json);
//            } else {
//                removeList.add(con);
//            }
//        }
//
//        for (Connection con : removeList) {
//            connections.remove(con.authToken);
//        }
//    }

    public void sendToSelf(int gameID, String sendAuth, ServerMessage message) throws IOException {
        String json = new Gson().toJson(message);
        connections.get(gameID).get(sendAuth).send(json);
//        ArrayList<Connection> removeList = new ArrayList<Connection>();
//        for (Connection con : connections.get(gameID).values()) {
//            if (con.session.isOpen()) {
//                if (con.authToken.equals(sendAuth)) {
//                    System.out.println(message);
//                    String json = new Gson().toJson(message);
//                    con.send(json);
//                }
//            } else {
//                removeList.add(con);
//            }
//        }
//
//        for (Connection con : removeList) {
//            connections.remove(con.authToken);
//        }
    }
}