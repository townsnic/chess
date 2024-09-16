package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Establishes the logic of how chess pieces move
 */
public class PieceMoveLogic {

    /**
     * The various directions a piece can move
     * Not all pieces can move in all directions
     */
    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        UP_AND_LEFT,
        UP_AND_RIGHT,
        DOWN_AND_LEFT,
        DOWN_AND_RIGHT,
        LEFT_AND_UP,
        LEFT_AND_DOWN,
        RIGHT_AND_UP,
        RIGHT_AND_DOWN
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @param board the current chess board
     * @param myPosition the current position to check from
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece myPiece) {
        return new ArrayList<>();
    }

    /**
     * Ensures that a position is on the chess board
     *
     * @param row the row to check
     * @param col the column to check
     * @return if the position is on the chess board
     */
    public boolean onBoard(int row, int col) {
        return (0 < row && row < 9 && 0 < col && col < 9);
    }

    /**
     * Determines if a space is occupied
     *
     * @param board the current chess board
     * @param checkPosition the current position to check
     * @return if the position is occupied
     */
    public boolean spaceOccupied(ChessBoard board, ChessPosition checkPosition) {
        return board.getPiece(checkPosition) != null;
    }

    /**
     * Determines if a space is occupied by a piece of the same team
     *
     * @param board the current chess board
     * @param checkPosition the current position to check
     * @param myPiece the chess piece in play
     * @return if the position is occupied by a piece of the same team
     */
    public boolean friendlyFire(ChessBoard board, ChessPosition checkPosition, ChessPiece myPiece) {
        if (!spaceOccupied(board, checkPosition)) {
            return false;
        }
        return board.getPiece(checkPosition).getTeamColor() == myPiece.getTeamColor();
    }
}
