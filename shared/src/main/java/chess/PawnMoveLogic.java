package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Establishes the logic of how a pawn moves
 */
public class PawnMoveLogic extends PieceMoveLogic {

    /**
     * Determines if a pawn has moved
     *
     * @param myPiece the pawn
     * @param startRow the row the pawn is at
     * @return if the pawn is taking its first move
     */
    public boolean firstMove(ChessPiece myPiece, int startRow) {
        return (myPiece.getTeamColor() == ChessGame.TeamColor.WHITE && startRow == 2)
                || (myPiece.getTeamColor() == ChessGame.TeamColor.BLACK && startRow == 7);
    }

    /**
     * Determines if a pawn is moving to a promotion square
     *
     * @param myPiece the pawn
     * @param newRow the row the pawn is moving to
     * @return if the pawn is moving to a promotion square
     */
    public boolean promotionSquare(ChessPiece myPiece, int newRow) {
        return (myPiece.getTeamColor() == ChessGame.TeamColor.WHITE && newRow == 8)
                || (myPiece.getTeamColor() == ChessGame.TeamColor.BLACK && newRow == 1);
    }

    /**
     * Compiles all possible promotions into a ChessMove array
     *
     * @param myPosition the position the pawn is at
     * @param endPosition the position the pawn is moving to
     * @return a collection of all possible promotions
     */
    public Collection<ChessMove> addAllPromotions(ChessPosition myPosition, ChessPosition endPosition) {
        ArrayList<ChessMove> promotions = new ArrayList<>();
        promotions.add(new ChessMove(myPosition, endPosition, ChessPiece.PieceType.QUEEN));
        promotions.add(new ChessMove(myPosition, endPosition, ChessPiece.PieceType.ROOK));
        promotions.add(new ChessMove(myPosition, endPosition, ChessPiece.PieceType.BISHOP));
        promotions.add(new ChessMove(myPosition, endPosition, ChessPiece.PieceType.KNIGHT));

        return promotions;
    }

    /**
     * Determines if a pawn can attack
     *
     * @param board the current chess board
     * @param startRow the row the pawn is at
     * @param startCol the column the pawn is at
     * @param myPosition the pawn's current position
     * @param myPiece the pawn
     * @param path the direction of interest
     * @return collection of legal attacking moves
     */
    public Collection<ChessMove> testAttack(ChessBoard board, int startRow, int startCol,
                                   ChessPosition myPosition, ChessPiece myPiece, Direction path) {
        ArrayList<ChessMove> attacks = new ArrayList<>();
        int rowIncrement;
        int colIncrement;

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
            throw new RuntimeException("A pawn cannot attack in that direction!");
        }

        int attackRow = startRow + rowIncrement;
        int attackCol = startCol + colIncrement;
        ChessPosition attackPosition = new ChessPosition(attackRow, attackCol);
        if (spaceOccupied(board, attackPosition) && !friendlyFire(board, attackPosition, myPiece)) {
            if (promotionSquare(myPiece, attackRow)) {
                attacks.addAll(addAllPromotions(myPosition, attackPosition));
            }
            attacks.add(new ChessMove(myPosition, attackPosition, null));
        }

        return attacks;
    }

    /**
     * Calculates all the positions a pawn can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @param board the current chess board
     * @param myPosition the pawn's position
     * @return collection of valid moves
     */
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece myPiece) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        int startPositionRow = myPosition.getRow();
        int startPositionCol = myPosition.getColumn();

        // Check forward 1
        // Check if first move
        // Check forward 2
        
        // Check attacks
        if (myPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            validMoves.addAll(testAttack(board, startPositionRow, startPositionCol,
                    myPosition, myPiece, Direction.UP_AND_LEFT));
            validMoves.addAll(testAttack(board, startPositionRow, startPositionCol,
                    myPosition, myPiece, Direction.UP_AND_RIGHT));
        } else if (myPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            validMoves.addAll(testAttack(board, startPositionRow, startPositionCol,
                    myPosition, myPiece, Direction.DOWN_AND_LEFT));
            validMoves.addAll(testAttack(board, startPositionRow, startPositionCol,
                    myPosition, myPiece, Direction.DOWN_AND_RIGHT));
        }

        // Check promotion


        return validMoves;
    }
}
