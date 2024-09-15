package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoveLogic extends PieceMoveLogic {

    /**
     * Calculates all the positions a bishop can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece myPiece) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        int startPositionRow = myPosition.getRow();
        int startPositionCol = myPosition.getColumn();

        // Up and left
        int legalPositionRow = startPositionRow + 1;
        int legalPositionCol = startPositionCol - 1;
        while (legalPosition(legalPositionRow, legalPositionCol)) {
            ChessPosition goodPosition = new ChessPosition(legalPositionRow, legalPositionCol);
            ChessMove goodMove = new ChessMove(myPosition, goodPosition, null);
            if (board.getPiece(goodPosition) != null) {
                if (board.getPiece(goodPosition).getTeamColor() != myPiece.getTeamColor()) {
                    validMoves.add(goodMove);
                }
                break;
            }
            validMoves.add(goodMove);
            legalPositionRow += 1;
            legalPositionCol -= 1;
        }

        // Up and right
        legalPositionRow = startPositionRow + 1;
        legalPositionCol = startPositionCol + 1;
        while (legalPosition(legalPositionRow, legalPositionCol)) {
            ChessPosition goodPosition = new ChessPosition(legalPositionRow, legalPositionCol);
            ChessMove goodMove = new ChessMove(myPosition, goodPosition, null);
            if (board.getPiece(goodPosition) != null) {
                if (board.getPiece(goodPosition).getTeamColor() != myPiece.getTeamColor()) {
                    validMoves.add(goodMove);
                }
                break;
            }
            validMoves.add(goodMove);
            legalPositionRow += 1;
            legalPositionCol += 1;
        }

        // Down and left
        legalPositionRow = startPositionRow - 1;
        legalPositionCol = startPositionCol - 1;
        while (legalPosition(legalPositionRow, legalPositionCol)) {
            ChessPosition goodPosition = new ChessPosition(legalPositionRow, legalPositionCol);
            ChessMove goodMove = new ChessMove(myPosition, goodPosition, null);
            if (board.getPiece(goodPosition) != null) {
                if (board.getPiece(goodPosition).getTeamColor() != myPiece.getTeamColor()) {
                    validMoves.add(goodMove);
                }
                break;
            }
            validMoves.add(goodMove);
            legalPositionRow -= 1;
            legalPositionCol -= 1;
        }

        // Down and right
        legalPositionRow = startPositionRow - 1;
        legalPositionCol = startPositionCol + 1;
        while (legalPosition(legalPositionRow, legalPositionCol)) {
            ChessPosition goodPosition = new ChessPosition(legalPositionRow, legalPositionCol);
            ChessMove goodMove = new ChessMove(myPosition, goodPosition, null);
            if (board.getPiece(goodPosition) != null) {
                if (board.getPiece(goodPosition).getTeamColor() != myPiece.getTeamColor()) {
                    validMoves.add(goodMove);
                }
                break;
            }
            validMoves.add(goodMove);
            legalPositionRow -= 1;
            legalPositionCol += 1;
        }

        return validMoves;
    }
}