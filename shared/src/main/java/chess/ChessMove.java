package chess;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * Overrides the equals method to compare attributes of ChessMove
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessMove move = (ChessMove) o;
        return (startPosition.equals(move.startPosition) && endPosition.equals(move.endPosition)
                && promotionPiece == move.promotionPiece);
    }

    /**
     * Overrides the hash method for larger hash spread
     */
    @Override
    public int hashCode() {
        int promotionCode = promotionPiece == null ? 97 : promotionPiece.hashCode();
        return 97 * (startPosition.hashCode() + endPosition.hashCode() + promotionCode);
    }

    /**
     * Overrides the toString method to print ChessMove start, end, and promotion piece
     */
    @Override
    public String toString() {
        String newPiece = (promotionPiece == null ? "" : " " + promotionPiece.toString());
        return String.format("%s->%s%s", startPosition.toString(), endPosition.toString(), newPiece);
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        throw new RuntimeException("Not implemented");
    }
}
