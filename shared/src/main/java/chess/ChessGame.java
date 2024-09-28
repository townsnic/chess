package chess;

import java.util.Collection;

/**
 * A class that can manage a chess game, making moves on a board
 */
public class ChessGame {

    private ChessBoard board = new ChessBoard();
    private TeamColor turn;

    public ChessGame() {
        this.turn = TeamColor.WHITE;
        board.resetBoard();
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * @return which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        if (turn == TeamColor.WHITE) {
            turn = TeamColor.BLACK;
        } else {
            turn = TeamColor.WHITE;
        }
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> validMoves;
        ChessPiece myPiece = board.getPiece(startPosition);
        validMoves = myPiece.pieceMoves(board, startPosition);
        // Check for Check
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return true if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingSpace = findKing(teamColor);
        for (int row = 1; row < 9; ++row) {
            for (int col = 1; col < 9; ++col) {
                ChessPosition checkPosition = new ChessPosition(row, col);
                if (PieceMoveLogic.spaceOccupied(board, checkPosition)) {
                    ChessPiece piece = board.getPiece(checkPosition);
                    if (piece.getTeamColor() != teamColor) {
                        Collection<ChessMove> validMoves = piece.pieceMoves(board, checkPosition);
                        if (piece.getPieceType() != ChessPiece.PieceType.PAWN) {
                            if (validMoves.contains(new ChessMove(checkPosition, kingSpace, null))) {
                                return true;
                            }
                        } else {
                            if (validMoves.contains(new ChessMove(checkPosition, kingSpace, ChessPiece.PieceType.QUEEN))) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return true if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return true if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    /**
     * Finds the position of the king of a given color
     *
     * @param teamColor which team's king to look for
     * @return the position of the king
     */
    private ChessPosition findKing(TeamColor teamColor) {
        ChessPiece king = new ChessPiece(teamColor, ChessPiece.PieceType.KING);
        ChessPosition kingSpace = null;
        for (int row = 1; row < 9; ++row) {
            for (int col = 1; col < 9; ++col) {
                ChessPosition possibleSpace = new ChessPosition(row, col);
                if (PieceMoveLogic.spaceOccupied(board, possibleSpace)) {
                    if (board.getPiece(possibleSpace).equals(king)) {
                        kingSpace = possibleSpace;
                    }
                }
            }
        }
        return kingSpace;
    }
}
