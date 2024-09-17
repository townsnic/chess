package chess;

import java.util.Collection;

/**
 * Represents a single chess piece
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
     *
     * @param obj the object to compare current instantiation to
     * @return if the objects are equal
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
     *
     * @return the new hash code
     */
    @Override
    public int hashCode() {
        return 97 * (pieceColor.hashCode() + type.hashCode());
    }

    /**
     * Overrides the toString method to print ChessPiece color and type
     *
     * @return a string of the color and type of ChessPiece
     */
    @Override
    public String toString() {
        return String.format("%s %s", pieceColor.toString(), type.toString());
    }

    /**
     * @return which team this chess piece belongs to
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
     * @param board the current chess board
     * @param myPosition the current position to check from
     * @return collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (type) {
            case KING -> {
                KingMoveLogic kingMove = new KingMoveLogic();
                yield kingMove.pieceMoves(board, myPosition, this);
            }
            case QUEEN -> {
                QueenMoveLogic queenMove = new QueenMoveLogic();
                yield queenMove.pieceMoves(board, myPosition, this);
            }
            case BISHOP -> {
                BishopMoveLogic bishopMove = new BishopMoveLogic();
                yield bishopMove.pieceMoves(board, myPosition, this);
            }
            case KNIGHT -> {
                KnightMoveLogic knightMove = new KnightMoveLogic();
                yield knightMove.pieceMoves(board, myPosition, this);
            }
            case ROOK -> {
                RookMoveLogic rookMove = new RookMoveLogic();
                yield rookMove.pieceMoves(board, myPosition, this);
            }
            case PAWN -> {
                PawnMoveLogic pawnMove = new PawnMoveLogic();
                yield pawnMove.pieceMoves(board, myPosition, this);
            }
        };
    }
}
