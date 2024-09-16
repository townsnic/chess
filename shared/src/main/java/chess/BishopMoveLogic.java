package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Establishes the logic of how a bishop moves
 */
public class BishopMoveLogic extends PieceMoveLogic {

    /**
     * Calculates all the positions a bishop can move to in a single direction
     *
     * @param board the current chess board
     * @param startRow the row the bishop is at
     * @param startCol the column the bishop is at
     * @param myPosition the bishop's current position
     * @param myPiece the bishop
     * @param path the direction of interest
     * @return collection of valid moves in a single direction
     */
    private Collection<ChessMove> testDirection(ChessBoard board, int startRow, int startCol,
                                                 ChessPosition myPosition, ChessPiece myPiece, Direction path) {
        ArrayList<ChessMove> oneDirectionMoves = new ArrayList<>();
        int rowIncrement;
        int colIncrement;

        // Determine move direction
        if (path == Direction.UP_AND_LEFT) {
            rowIncrement = 1;
            colIncrement = -1;
        } else if (path == Direction.UP_AND_RIGHT) {
            rowIncrement = 1;
            colIncrement = 1;
        } else if (path == Direction.DOWN_AND_LEFT) {
            rowIncrement = -1;
            colIncrement = -1;
        } else if (path == Direction.DOWN_AND_RIGHT) {
            rowIncrement = -1;
            colIncrement = 1;
        } else {
            throw new RuntimeException("A bishop cannot move in that direction!");
        }

        int newRow = startRow + rowIncrement;
        int newCol = startCol + colIncrement;
        // Ensure potential position is on board
        while (onBoard(newRow, newCol)) {
            ChessPosition goodPosition = new ChessPosition(newRow, newCol);
            // Check if space is occupied
            if (spaceOccupied(board, goodPosition)) {
                // Ensure the position is not occupied by same team piece
                if (!friendlyFire(board, goodPosition, myPiece)) {
                    oneDirectionMoves.add(new ChessMove(myPosition, goodPosition, null));
                }
                break;
            }
            oneDirectionMoves.add(new ChessMove(myPosition, goodPosition, null));
            newRow += rowIncrement;
            newCol += colIncrement;
        }
        return oneDirectionMoves;
    }

    /**
     * Calculates all the positions a bishop can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @param board the current chess board
     * @param myPosition the bishop's position
     * @return collection of valid moves
     */
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece myPiece) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        int startPositionRow = myPosition.getRow();
        int startPositionCol = myPosition.getColumn();

        validMoves.addAll(testDirection(board, startPositionRow,
                startPositionCol, myPosition, myPiece, Direction.UP_AND_LEFT));
        validMoves.addAll(testDirection(board, startPositionRow,
                startPositionCol, myPosition, myPiece, Direction.UP_AND_RIGHT));
        validMoves.addAll(testDirection(board, startPositionRow,
                startPositionCol, myPosition, myPiece, Direction.DOWN_AND_LEFT));
        validMoves.addAll(testDirection(board, startPositionRow,
                startPositionCol, myPosition, myPiece, Direction.DOWN_AND_RIGHT));

        return validMoves;
    }
}