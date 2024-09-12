package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMoveLogic {


    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return new ArrayList<>();
    }

    // Ensures that a position is on the chess board
    public boolean legalPosition(int row, int col) {
        return (0 < row && row < 9 && 0 < col && col < 9);
    }
}
