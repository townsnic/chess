package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Establishes the logic of how a king moves
 */
public class KingMoveLogic extends PieceMoveLogic {

    /**
     * Determines if a king can move to in a given direction
     *
     * @param board the current chess board
     * @param startRow the row the king is at
     * @param startCol the column the king is at
     * @param myPosition the king's current position
     * @param myPiece the king
     * @param path the direction of interest
     * @return the move in the specified direction, if legal. If illegal, null
     */
    private ChessMove testDirection(ChessBoard board, int startRow, int startCol,
                          ChessPosition myPosition, ChessPiece myPiece, Direction path) {
        int[] increments = setIncrements(path, myPiece);
        int rowIncrement = increments[0];
        int colIncrement = increments[1];
        return justOneMove(board, myPiece, myPosition, startRow, startCol, rowIncrement, colIncrement);
    }

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
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        ArrayList<ChessMove> potentialMoves = new ArrayList<>();
        int startPositionRow = myPosition.getRow();
        int startPositionCol = myPosition.getColumn();

        potentialMoves.add(testDirection(board, startPositionRow,
                startPositionCol, myPosition, myPiece, Direction.UP));
        potentialMoves.add(testDirection(board, startPositionRow,
                startPositionCol, myPosition, myPiece, Direction.DOWN));
        potentialMoves.add(testDirection(board, startPositionRow,
                startPositionCol, myPosition, myPiece, Direction.LEFT));
        potentialMoves.add(testDirection(board, startPositionRow,
                startPositionCol, myPosition, myPiece, Direction.RIGHT));
        potentialMoves.add(testDirection(board, startPositionRow,
                startPositionCol, myPosition, myPiece, Direction.UP_AND_LEFT));
        potentialMoves.add(testDirection(board, startPositionRow,
                startPositionCol, myPosition, myPiece, Direction.UP_AND_RIGHT));
        potentialMoves.add(testDirection(board, startPositionRow,
                startPositionCol, myPosition, myPiece, Direction.DOWN_AND_LEFT));
        potentialMoves.add(testDirection(board, startPositionRow,
                startPositionCol, myPosition, myPiece, Direction.DOWN_AND_RIGHT));

        // Remove null moves from collection
        for (ChessMove move : potentialMoves) {
            if (move != null) {
                validMoves.add(move);
            }
        }
        return validMoves;
    }
}
