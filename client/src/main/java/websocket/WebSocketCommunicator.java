package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import org.eclipse.jetty.server.Authentication;
import websocket.messages.*;
import websocket.commands.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketCommunicator extends Endpoint {

    Session session;
    ServerMessageObserver serverMessageObserver;
    Gson gson = new Gson();

    public WebSocketCommunicator(String url, ServerMessageObserver serverMessageObserver) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.serverMessageObserver = serverMessageObserver;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                public void onMessage(String msg) {
                    try {
                        serverMessageObserver.notify(msg);
                    } catch(Exception ex) {
                        serverMessageObserver.notify(gson.toJson(new ErrorMessage(ex.getMessage())));
                    }
                }

            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void joinGame(String authToken, int gameID) throws Exception {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(gson.toJson(command));
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public void leaveGame(String authToken, int gameID) throws Exception {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(gson.toJson(command));
            this.session.close();
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public void resign(String authToken, int gameID) throws Exception {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(gson.toJson(command));
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public void move(String authToken, int gameID, ChessMove move) throws Exception {
        try {
            MakeMoveCommand command = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move);
            this.session.getBasicRemote().sendText(gson.toJson(command));
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }

}