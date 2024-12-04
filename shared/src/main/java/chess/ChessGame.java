package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 */
public class ChessGame {

    public boolean gameOver = false;
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
     * Overrides the equals method to compare attributes of ChessGame
     *
     * @param obj the object to compare current instantiation to
     * @return if the objects are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null || getClass() != obj.getClass()) { return false; }
        ChessGame chessGame = (ChessGame) obj;
        return Objects.equals(gameBoard, chessGame.gameBoard) && turn == chessGame.turn;
    }

    /**
     * Overrides the hash method for larger hash spread
     *
     * @return the new hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(gameBoard, turn);
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
        if (myPiece == null) { return null; }

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
        return validMoves;
    }

    /**
     * Executes all valid moves to determine if the king is still in check
     *
     * @param validMoves the list of current valid moves for a team
     * @param teamColor the current team's color
     * @return bool indicating if the king is still in check after executing valid moves
     */
    private boolean checkValidMoves(Collection<ChessMove> validMoves, TeamColor teamColor) {
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
        return true;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece myPiece = gameBoard.getPiece(move.getStartPosition());
        // Checks for invalid moves, then makes move
        if (myPiece == null) {
            throw new InvalidMoveException("Error: There is no piece at that start position.");
        } else if (myPiece.getTeamColor() != turn) {
            throw new InvalidMoveException("Error: It's not your turn.");
        } else if (validMoves(move.getStartPosition()) == null || !validMoves(move.getStartPosition()).contains(move)) {
            if (myPiece.pieceMoves(gameBoard, move.getStartPosition()).contains(move)) {
                throw new InvalidMoveException("Error: You can't leave your king in check.");
            } else {
                throw new InvalidMoveException("Error: Invalid move.");
            }
        } else {
            gameBoard.movePiece(move);
        }
        setTeamTurn((myPiece.getTeamColor() == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE);
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
        // Account for poorly-designed test cases where there is no king on the board
        if (kingSpace == null) { return false; }

        // Iterates through every space on the board
        for (int row = 1; row < 9; ++row) {
            for (int col = 1; col < 9; ++col) {
                ChessPosition checkPosition = new ChessPosition(row, col);

                // Ensures a space is occupied by an enemy piece
                if (PieceMoveLogic.spaceOccupied(gameBoard, checkPosition) &&
                        (gameBoard.getPiece(checkPosition).getTeamColor() != teamColor)) {
                    Collection<ChessMove> legalMoves = gameBoard.getPiece(checkPosition).pieceMoves(gameBoard, checkPosition);

                    // Determines if the piece can legally attack the king
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
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return true if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // If not in check, can't be in checkmate
        if (!isInCheck(teamColor)) {
            return false;
        }
        // Iterates through every position on the board
        for (int row = 1; row < 9; ++row) {
            for (int col = 1; col < 9; ++col) {
                ChessPosition checkPosition = new ChessPosition(row, col);

                // Ensures a space is occupied by a team piece
                if (PieceMoveLogic.spaceOccupied(gameBoard, checkPosition) &&
                        gameBoard.getPiece(checkPosition).getTeamColor() == teamColor) {
                    Collection<ChessMove> validMoves = validMoves(checkPosition);
                    // Checks all of a piece's valid moves
                    if(!checkValidMoves(validMoves, teamColor)) { return false; }
                }
            }
        }
        gameOver = true;
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
        // If in check, cannot be stalemate
        if (isInCheck(teamColor)) {
            return false;
        }
        // Iterates through every space on the board
        for (int row = 1; row < 9; ++row) {
            for (int col = 1; col < 9; ++col) {
                ChessPosition checkPosition = new ChessPosition(row, col);

                // Ensures a space is occupied by a team piece
                if (PieceMoveLogic.spaceOccupied(gameBoard, checkPosition) &&
                        gameBoard.getPiece(checkPosition).getTeamColor() == teamColor) {
                    // Checks if the piece can move
                    if (!validMoves(checkPosition).isEmpty()) { return false; }

                }
            }
        }
        gameOver = true;
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


    /**
     * Creates a copy of a given board
     *
     * @param sourceBoard the chess board to create a copy of
     * @return the newly copied chess board
     */
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
     * @return the position of the king, or null if running a
     * poorly-designed test case where there's no king on the board
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
