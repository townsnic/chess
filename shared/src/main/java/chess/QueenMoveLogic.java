package chess;

import java.util.Collection;

/**
 * Establishes the logic of how a queen moves
 */
public class QueenMoveLogic extends PieceMoveLogic {

    /**
     * Calculates all the positions a queen can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @param board the current chess board
     * @param myPosition the queen's position
     * @return collection of valid moves
     */
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece myPiece) {
        // Directions a queen can move
        Direction[] possibleDirections = {Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT,
                Direction.UP_AND_LEFT, Direction.UP_AND_RIGHT, Direction.DOWN_AND_LEFT, Direction.DOWN_AND_RIGHT};
        return calculateValidMoves(possibleDirections, myPiece, myPosition, board);
    }
}