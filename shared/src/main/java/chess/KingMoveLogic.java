package chess;

import java.util.Collection;

/**
 * Establishes the logic of how a king moves
 */
public class KingMoveLogic extends PieceMoveLogic {

    /**
     * Calculates all the positions a king can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @param board the current chess board
     * @param myPosition the king's position
     * @return collection of valid moves
     */
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece myPiece) {
        // Directions a king can move
        Direction[] possibleDirections = {Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT,
        Direction.UP_AND_LEFT, Direction.UP_AND_RIGHT, Direction.DOWN_AND_LEFT, Direction.DOWN_AND_RIGHT};
        return calculateValidMoves(possibleDirections, myPiece, myPosition, board);
    }
}
