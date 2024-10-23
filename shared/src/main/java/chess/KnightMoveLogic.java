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
        validMoves.addAll(testDirection(board, startPositionRow,
                startPositionCol, myPosition, myPiece, Direction.LEFT_AND_UP));
        validMoves.addAll(testDirection(board, startPositionRow,
                startPositionCol, myPosition, myPiece, Direction.LEFT_AND_DOWN));
        validMoves.addAll(testDirection(board, startPositionRow,
                startPositionCol, myPosition, myPiece, Direction.RIGHT_AND_UP));
        validMoves.addAll(testDirection(board, startPositionRow,
                startPositionCol, myPosition, myPiece, Direction.RIGHT_AND_DOWN));

        return validMoves;
    }
}
