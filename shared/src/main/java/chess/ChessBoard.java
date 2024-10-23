package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces
 */
public class ChessBoard {

    private final ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {
    }

    /**
     * Overrides the equals method to compare attributes of ChessBoard
     *
     * @param obj the object to compare current instantiation to
     * @return if the objects are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null || getClass() != obj.getClass()) { return false; }
        ChessBoard board = (ChessBoard) obj;
        return Arrays.deepEquals(squares, board.squares);
    }

    /**
     * Overrides the hash method for larger hash spread
     *
     * @return the new hash code
     */
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    /**
     * Overrides the toString method to print the current state of the chess board
     *
     * @return a string depiction of the chess board
     */
    @Override
    public String toString() {
        String[][] pieces = new String[8][8];
        for (int row = 0; row < 8; ++row) {
            for (int col = 0; col < 8; ++col) {
                if (squares[row][col] == null) {
                    pieces[row][col] = " ";
                }
                else {
                    switch (squares[row][col].getTeamColor()) {
                        case WHITE:
                            switch (squares[row][col].getPieceType()) {
                                case KING:
                                    pieces[row][col] = "K";
                                    break;
                                case QUEEN:
                                    pieces[row][col] = "Q";
                                    break;
                                case BISHOP:
                                    pieces[row][col] = "B";
                                    break;
                                case KNIGHT:
                                    pieces[row][col] = "N";
                                    break;
                                case ROOK:
                                    pieces[row][col] = "R";
                                    break;
                                case PAWN:
                                    pieces[row][col] = "P";
                                    break;
                            }
                            break;
                        case BLACK:
                            switch (squares[row][col].getPieceType()) {
                                case KING:
                                    pieces[row][col] = "k";
                                    break;
                                case QUEEN:
                                    pieces[row][col] = "q";
                                    break;
                                case BISHOP:
                                    pieces[row][col] = "b";
                                    break;
                                case KNIGHT:
                                    pieces[row][col] = "n";
                                    break;
                                case ROOK:
                                    pieces[row][col] = "r";
                                    break;
                                case PAWN:
                                    pieces[row][col] = "p";
                                    break;
                            }
                            break;
                    }
                }


            }
        }
        return String.format("""
                |%s|%s|%s|%s|%s|%s|%s|%s|
                |%s|%s|%s|%s|%s|%s|%s|%s|
                |%s|%s|%s|%s|%s|%s|%s|%s|
                |%s|%s|%s|%s|%s|%s|%s|%s|
                |%s|%s|%s|%s|%s|%s|%s|%s|
                |%s|%s|%s|%s|%s|%s|%s|%s|
                |%s|%s|%s|%s|%s|%s|%s|%s|
                |%s|%s|%s|%s|%s|%s|%s|%s|
                """, pieces[7][0], pieces[7][1], pieces[7][2], pieces[7][3], pieces[7][4], pieces[7][5], pieces[7][6],
                pieces[7][7], pieces[6][0], pieces[6][1], pieces[6][2], pieces[6][3], pieces[6][4], pieces[6][5],
                pieces[6][6], pieces[6][7], pieces[5][0], pieces[5][1], pieces[5][2], pieces[5][3], pieces[5][4],
                pieces[5][5], pieces[5][6], pieces[5][7], pieces[4][0], pieces[4][1], pieces[4][2], pieces[4][3],
                pieces[4][4], pieces[4][5], pieces[4][6], pieces[4][7], pieces[3][0], pieces[3][1], pieces[3][2],
                pieces[3][3], pieces[3][4], pieces[3][5], pieces[3][6], pieces[3][7], pieces[2][0], pieces[2][1],
                pieces[2][2], pieces[2][3], pieces[2][4], pieces[2][5], pieces[2][6], pieces[2][7], pieces[1][0],
                pieces[1][1], pieces[1][2], pieces[1][3], pieces[1][4], pieces[1][5], pieces[1][6], pieces[1][7],
                pieces[0][0], pieces[0][1], pieces[0][2], pieces[0][3], pieces[0][4], pieces[0][5], pieces[0][6],
                pieces[0][7]);
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        if (position.getRow() < 1 || position.getRow() > 8 || position.getColumn() < 1 || position.getColumn() > 8) {
            throw new RuntimeException("Cannot add piece, position out of bounds!");
        }
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        if (position.getRow() < 1 || position.getRow() > 8 || position.getColumn() < 1 || position.getColumn() > 8) {
            throw new RuntimeException("Cannot get piece, position out of bounds!");
        } else {
            return squares[position.getRow() - 1][position.getColumn() - 1];
        }
    }

    /**
     * Moves a piece on the chess board
     *
     * @param move The move to be made
     */
    public void movePiece(ChessMove move) {
        // Start Position
        ChessPosition startPosition = move.getStartPosition();
        int startRow = startPosition.getRow();
        int startCol = startPosition.getColumn();
        // End Position
        ChessPosition endPosition = move.getEndPosition();
        int endRow = endPosition.getRow();
        int endCol = endPosition.getColumn();
        // Piece
        ChessPiece myPiece = getPiece(startPosition);

        // Set the current position to null
        squares[startRow - 1][startCol - 1] = null;

        // Move the piece or exchange it for its promotion piece
        if (move.getPromotionPiece() == null) {
            squares[endRow - 1][endCol - 1] = myPiece;
        } else {
            squares[endRow - 1][endCol - 1] = new ChessPiece(myPiece.getTeamColor(), move.getPromotionPiece());
        }
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        ChessPosition[][] allPositions = new ChessPosition[8][8];

        // Create array of positions, set every space to null
        for (int row = 1; row < 9; ++row) {
            for (int col = 1; col < 9; ++col) {
                squares[row-1][col-1] = null;
                allPositions[row-1][col-1] = new ChessPosition(row, col);
            }
        }

        // Rows 2 & 7
        for (int pawn = 0; pawn < 8; ++pawn) {
            addPiece(allPositions[1][pawn], new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
            addPiece(allPositions[6][pawn], new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }

        //Row 1
        addPiece(allPositions[0][0], new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(allPositions[0][1], new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(allPositions[0][2], new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(allPositions[0][3], new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(allPositions[0][4], new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        addPiece(allPositions[0][5], new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(allPositions[0][6], new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(allPositions[0][7], new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        // Row 8
        addPiece(allPositions[7][0], new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(allPositions[7][1], new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(allPositions[7][2], new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(allPositions[7][3], new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        addPiece(allPositions[7][4], new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        addPiece(allPositions[7][5], new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(allPositions[7][6], new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(allPositions[7][7], new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
    }
}
