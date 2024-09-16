package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Establishes the logic of how a knight moves
 */
public class KnightMoveLogic extends PieceMoveLogic {

    /**
     * Determines if a knight can move to in a given direction
     *
     * @param board the current chess board
     * @param startRow the row the knight is at
     * @param startCol the column the knight is at
     * @param myPosition the knight's current position
     * @param myPiece the knight
     * @param path the direction of interest
     * @return the move in the specified direction, if legal. If illegal, null
     */
    public ChessMove testDirection(ChessBoard board, int startRow, int startCol,
                                   ChessPosition myPosition, ChessPiece myPiece, Direction path) {
        int rowIncrement;
        int colIncrement;

        // Determine move direction
        if (path == Direction.UP_AND_LEFT) {
            rowIncrement = 2;
            colIncrement = -1;
        } else if (path == Direction.UP_AND_RIGHT) {
            rowIncrement = 2;
            colIncrement = 1;
        } else if (path == Direction.DOWN_AND_LEFT) {
            rowIncrement = -2;
            colIncrement = -1;
        } else if (path == Direction.DOWN_AND_RIGHT) {
            rowIncrement = -2;
            colIncrement = 1;
        } else if (path == Direction.LEFT_AND_UP) {
            rowIncrement = 1;
            colIncrement = -2;
        } else if (path == Direction.LEFT_AND_DOWN) {
            rowIncrement = -1;
            colIncrement = -2;
        } else if (path == Direction.RIGHT_AND_UP) {
            rowIncrement = 1;
            colIncrement = 2;
        } else if (path == Direction.RIGHT_AND_DOWN) {
            rowIncrement = -1;
            colIncrement = 2;
        } else {
            throw new RuntimeException("A knight cannot move in that direction!");
        }

        // Ensure potential position is on the board
        if (onBoard(startRow + rowIncrement, startCol + colIncrement)) {
            ChessPosition goodPosition = new ChessPosition(startRow + rowIncrement, startCol + colIncrement);
            // Ensure the position is not occupied by same team piece
            if (!friendlyFire(board, goodPosition, myPiece)) {
                return new ChessMove(myPosition, goodPosition, null);
            }
        }
        return null;
    }

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
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        ArrayList<ChessMove> potentialMoves = new ArrayList<>();
        int startPositionRow = myPosition.getRow();
        int startPositionCol = myPosition.getColumn();

        potentialMoves.add(testDirection(board, startPositionRow,
                startPositionCol, myPosition, myPiece, Direction.UP_AND_LEFT));
        potentialMoves.add(testDirection(board, startPositionRow,
                startPositionCol, myPosition, myPiece, Direction.UP_AND_RIGHT));
        potentialMoves.add(testDirection(board, startPositionRow,
                startPositionCol, myPosition, myPiece, Direction.DOWN_AND_LEFT));
        potentialMoves.add(testDirection(board, startPositionRow,
                startPositionCol, myPosition, myPiece, Direction.DOWN_AND_RIGHT));
        potentialMoves.add(testDirection(board, startPositionRow,
                startPositionCol, myPosition, myPiece, Direction.LEFT_AND_UP));
        potentialMoves.add(testDirection(board, startPositionRow,
                startPositionCol, myPosition, myPiece, Direction.LEFT_AND_DOWN));
        potentialMoves.add(testDirection(board, startPositionRow,
                startPositionCol, myPosition, myPiece, Direction.RIGHT_AND_UP));
        potentialMoves.add(testDirection(board, startPositionRow,
                startPositionCol, myPosition, myPiece, Direction.RIGHT_AND_DOWN));

        // Remove null moves from collection
        for (ChessMove move : potentialMoves) {
            if (move != null) {
                validMoves.add(move);
            }
        }
        return validMoves;
    }
}
