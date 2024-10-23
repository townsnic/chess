package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
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
     *
     * @param obj the object to compare current instantiation to
     * @return if the objects are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null || getClass() != obj.getClass()) { return false; }
        ChessMove move = (ChessMove) obj;
        return (startPosition.equals(move.startPosition) && endPosition.equals(move.endPosition)
                && promotionPiece == move.promotionPiece);
    }

    /**
     * Overrides the hash method for larger hash spread
     *
     * @return the new hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }

    /**
     * Overrides the toString method to print ChessMove start, end, and promotion piece
     *
     * @return a string of the start position, end position, and promotion piece, if applicable
     */
    @Override
    public String toString() {
        String newPiece = (promotionPiece == null ? "" : " " + promotionPiece);
        return String.format("%s->%s%s", startPosition.toString(), endPosition.toString(), newPiece);
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }
}
