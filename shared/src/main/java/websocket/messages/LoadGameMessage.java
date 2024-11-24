package websocket.messages;

import chess.ChessGame;

import static websocket.messages.ServerMessage.ServerMessageType.LOAD_GAME;

public class LoadGameMessage extends ServerMessage {

    private final ChessGame game;

    public LoadGameMessage(ChessGame game) {
        super(LOAD_GAME);
        this.game = game;
    }

    public ChessGame getGame() {
        return game;
    }
}
