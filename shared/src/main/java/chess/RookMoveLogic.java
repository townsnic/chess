package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Establishes the logic of how a rook moves
 */
public class RookMoveLogic extends PieceMoveLogic {

    /**
     * Calculates all the positions a queen can move to in a single direction
     *
     * @param board the current chess board
     * @param startRow the row the rook is at
     * @param startCol the rook the queen is at
     * @param myPosition the rook's current position
     * @param myPiece the rook
     * @param path the direction of interest
     * @return collection of valid moves in a single direction
     */
    private Collection<ChessMove> testDirection(ChessBoard board, int startRow, int startCol,
                                                ChessPosition myPosition, ChessPiece myPiece, Direction path) {
        int[] increments = setIncrements(path, myPiece);
        int rowIncrement = increments[0];
        int colIncrement = increments[1];
        return asFarAsPossible(board, myPiece, myPosition, startRow, startCol, rowIncrement, colIncrement);
    }

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
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        int startPositionRow = myPosition.getRow();
        int startPositionCol = myPosition.getColumn();

        validMoves.addAll(testDirection(board, startPositionRow,
                startPositionCol, myPosition, myPiece, Direction.UP));
        validMoves.addAll(testDirection(board, startPositionRow,
                startPositionCol, myPosition, myPiece, Direction.DOWN));
        validMoves.addAll(testDirection(board, startPositionRow,
                startPositionCol, myPosition, myPiece, Direction.LEFT));
        validMoves.addAll(testDirection(board, startPositionRow,
                startPositionCol, myPosition, myPiece, Direction.RIGHT));

        return validMoves;
    }
}