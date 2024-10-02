package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A class that can manage a chess game, making moves on a board
 */
public class ChessGame {

    private ChessBoard gameBoard = new ChessBoard();
    private TeamColor turn;

    public ChessGame() {
        this.turn = TeamColor.WHITE;
        gameBoard.resetBoard();
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
        turn = team;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> potentialMoves;
        ChessPiece myPiece = gameBoard.getPiece(startPosition);
        TeamColor currentTeam = myPiece.getTeamColor();

        // Ensures there is a piece at that space
        if (myPiece == null) return null;

        potentialMoves = myPiece.pieceMoves(gameBoard, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>(potentialMoves);

        // Copies the chess board, makes a move, and then undoes the move
        for (ChessMove potentialMove : potentialMoves) {
            ChessBoard testBoard = copyBoard(gameBoard);
            gameBoard.movePiece(potentialMove);

            // If a move places the team in check, it is not valid
            if (isInCheck(currentTeam)) {
                validMoves.remove(potentialMove);
            }
            gameBoard = copyBoard(testBoard);
        }
        if (validMoves.isEmpty()) return null;
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece myPiece = gameBoard.getPiece(move.getStartPosition());
        if (myPiece == null) {
            throw new InvalidMoveException("There is no piece at that start position!");
        }
        else if (myPiece.getTeamColor() != turn) {
            throw new InvalidMoveException("It's not your turn!");
        } else if (validMoves(move.getStartPosition()) == null || !validMoves(move.getStartPosition()).contains(move)) {
            if (myPiece.pieceMoves(gameBoard, move.getStartPosition()).contains(move)) {
                throw new InvalidMoveException("You can't leave your king in check!");
            }
            throw new InvalidMoveException("Invalid move!");
        } else {
            gameBoard.movePiece(move);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return true if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // Locates the position of the king
        ChessPosition kingSpace = findKing(teamColor);

        // Iterates through every space on the board
        for (int row = 1; row < 9; ++row) {
            for (int col = 1; col < 9; ++col) {
                ChessPosition checkPosition = new ChessPosition(row, col);

                // Ensures a space is occupied by an enemy piece
                if (PieceMoveLogic.spaceOccupied(gameBoard, checkPosition)) {
                    ChessPiece piece = gameBoard.getPiece(checkPosition);
                    if (piece.getTeamColor() != teamColor) {
                        Collection<ChessMove> legalMoves = piece.pieceMoves(gameBoard, checkPosition);

                        // Checks if the piece is a pawn to account for promotions
                        if (piece.getPieceType() != ChessPiece.PieceType.PAWN) {
                            // Determines if the piece can legally attack the king
                            if (legalMoves.contains(new ChessMove(checkPosition, kingSpace, null))) {
                                return true;
                            }
                        } else {
                            // Determines if the pawn can legally attack the king
                            if (legalMoves.contains(new ChessMove(checkPosition, kingSpace, ChessPiece.PieceType.QUEEN)) ||
                                    legalMoves.contains(new ChessMove(checkPosition, kingSpace, ChessPiece.PieceType.ROOK)) ||
                                    legalMoves.contains(new ChessMove(checkPosition, kingSpace, ChessPiece.PieceType.BISHOP)) ||
                                    legalMoves.contains(new ChessMove(checkPosition, kingSpace, ChessPiece.PieceType.KNIGHT)) ||
                                    legalMoves.contains(new ChessMove(checkPosition, kingSpace, null))) {
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
        if (isInCheck(teamColor)) {
            for (int row = 1; row < 9; ++row) {
                for (int col = 1; col < 9; ++col) {
                    ChessPosition checkPosition = new ChessPosition(row, col);

                    // Ensures a space is occupied by a team piece
                    if (PieceMoveLogic.spaceOccupied(gameBoard, checkPosition)) {
                        ChessPiece piece = gameBoard.getPiece(checkPosition);

                        if (piece.getTeamColor() == teamColor) {
                            Collection<ChessMove> validMoves = validMoves(checkPosition);
                            if (validMoves != null) {
                                for (ChessMove validMove : validMoves) {
                                    ChessBoard testBoard = copyBoard(gameBoard);
                                    gameBoard.movePiece(validMove);

                                    // If a move removes the king from check, not checkmate
                                    if (!isInCheck(teamColor)) {
                                        return false;
                                    }
                                    gameBoard = copyBoard(testBoard);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return true if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // If a color is in check, it cannot be a stalemate
        if (isInCheck(teamColor)) {
            return false;
        }
        // Iterates through every space on the board
        for (int row = 1; row < 9; ++row) {
            for (int col = 1; col < 9; ++col) {
                ChessPosition checkPosition = new ChessPosition(row, col);

                // Ensures a space is occupied by a team piece
                if (PieceMoveLogic.spaceOccupied(gameBoard, checkPosition)) {
                    ChessPiece piece = gameBoard.getPiece(checkPosition);

                    // Checks if the piece can move
                    if (piece.getTeamColor() == teamColor) {
                        if (validMoves(checkPosition) != null) return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }

    public ChessBoard copyBoard(ChessBoard sourceBoard) {
        ChessBoard copyBoard = new ChessBoard();

        // Iterates through every space on the chess board and copies the relevant piece over
        for (int row = 1; row < 9; ++row) {
            for (int col = 1; col < 9; ++col) {
                ChessPosition copyPosition = new ChessPosition(row, col);
                copyBoard.addPiece(copyPosition, sourceBoard.getPiece(copyPosition));
            }
        }
        return copyBoard;
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

        // Iterates through every space on the chess board
        for (int row = 1; row < 9; ++row) {
            for (int col = 1; col < 9; ++col) {
                ChessPosition possibleSpace = new ChessPosition(row, col);

                // Ensures that a space is occupied and check if it is the correct king
                if (PieceMoveLogic.spaceOccupied(gameBoard, possibleSpace)) {
                    if (gameBoard.getPiece(possibleSpace).equals(king)) {
                        kingSpace = possibleSpace;
                    }
                }
            }
        }
        return kingSpace;
    }
}
