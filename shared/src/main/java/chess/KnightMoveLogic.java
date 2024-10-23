package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Establishes the logic of how a knight moves
 */
public class KnightMoveLogic extends PieceMoveLogic {

    /**
     * Calculates all the positions a knight can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @param board the current chess board
     * @param myPosition the knight's position
     * @return collection of valid moves
     */
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece myPiece) {
        // Directions a knight can move
        Direction[] possibleDirections = {Direction.UP_AND_LEFT, Direction.UP_AND_RIGHT, Direction.DOWN_AND_LEFT,
                Direction.DOWN_AND_RIGHT, Direction.LEFT_AND_UP, Direction.LEFT_AND_DOWN, Direction.RIGHT_AND_UP,
                Direction.RIGHT_AND_DOWN};
        return calculateValidMoves(possibleDirections, myPiece, myPosition, board);
    }
}
