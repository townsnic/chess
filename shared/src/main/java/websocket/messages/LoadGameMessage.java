package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage {

    private final ChessGame game;

    public LoadGameMessage(ServerMessageType messageType, ChessGame game) {
        super(messageType);
        this.game = game;
    }

    public ChessGame getGame() {
        return game;
    }
}
