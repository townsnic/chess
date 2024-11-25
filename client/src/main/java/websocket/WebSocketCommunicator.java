package websocket;

import com.google.gson.Gson;
import websocket.messages.*;
import websocket.commands.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketCommunicator extends Endpoint {

    Session session;
    ServerMessageObserver serverMessageObserver;

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
                        ServerMessage message = new Gson().fromJson(msg, ServerMessage.class);
                        serverMessageObserver.notify(message);
                    } catch(Exception ex) {
                        serverMessageObserver.notify(new ErrorMessage(ex.getMessage()));
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
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }
//
//    public void leaveGame(String visitorName) throws ResponseException {
//        try {
//            var action = new Action(Action.Type.EXIT, visitorName);
//            this.session.getBasicRemote().sendText(new Gson().toJson(action));
//            this.session.close();
//        } catch (IOException ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }

}