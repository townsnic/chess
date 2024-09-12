package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * Overrides the equals method to compare attributes of ChessPiece
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ChessPiece piece = (ChessPiece) obj;
        return (pieceColor.equals(piece.pieceColor) && type.equals(piece.type));
    }

    /**
     * Overrides the hash method for larger hash spread
     */
    @Override
    public int hashCode() {
        return 97 * (pieceColor.hashCode() + type.hashCode());
    }

    /**
     * Overrides the toString method to print ChessPiece color and type
     */
    @Override
    public String toString() {
        return String.format("%s %s", pieceColor.toString(), type.toString());
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch (type) {
            case KING:
                return new ArrayList<>();
            case QUEEN:
                return new ArrayList<>();
            case BISHOP:
                BishopMoveLogic bishopMove = new BishopMoveLogic();
                return bishopMove.pieceMoves(board, myPosition);
            case KNIGHT:
                return new ArrayList<>();
            case ROOK:
                return new ArrayList<>();
            case PAWN:
                return new ArrayList<>();
            default:
                throw new RuntimeException("Invalid Piece!");
        }
    }
}
