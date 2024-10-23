package chess;

import java.util.Collection;

/**
 * Establishes the logic of how a rook moves
 */
public class RookMoveLogic extends PieceMoveLogic {

    /**
     * Calculates all the positions a rook can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @param board the current chess board
     * @param myPosition the rook's position
     * @return collection of valid moves
     */
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece myPiece) {
        // Directions a rook can move
        Direction[] possibleDirections = {Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};
        return calculateValidMoves(possibleDirections, myPiece, myPosition, board);
    }
}