package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Establishes the logic of how chess pieces move
 */
public class PieceMoveLogic {

    /**
     * The various directions a piece can move
     * Not all pieces can move in all directions
     */
    protected enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        UP_AND_LEFT,
        UP_AND_RIGHT,
        DOWN_AND_LEFT,
        DOWN_AND_RIGHT,
        LEFT_AND_UP,
        LEFT_AND_DOWN,
        RIGHT_AND_UP,
        RIGHT_AND_DOWN
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
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece myPiece) {
        return new ArrayList<>();
    }

    /**
     * Ensures that a position is on the chess board
     *
     * @param row the row to check
     * @param col the column to check
     * @return if the position is on the chess board
     */
    protected boolean onBoard(int row, int col) {
        return (0 < row && row < 9 && 0 < col && col < 9);
    }

    /**
     * Determines if a space is occupied
     *
     * @param board the current chess board
     * @param checkPosition the current position to check
     * @return if the position is occupied
     */
    public static boolean spaceOccupied(ChessBoard board, ChessPosition checkPosition) {
        return board.getPiece(checkPosition) != null;
    }

    /**
     * Determines if a space is occupied by a piece of the same team
     *
     * @param board the current chess board
     * @param checkPosition the current position to check
     * @param myPiece the chess piece in play
     * @return if the position is occupied by a piece of the same team
     */
    protected boolean friendlyFire(ChessBoard board, ChessPosition checkPosition, ChessPiece myPiece) {
        if (!spaceOccupied(board, checkPosition)) {
            return false;
        }
        return board.getPiece(checkPosition).getTeamColor() == myPiece.getTeamColor();
    }

    /**
     * Sets the row and column increment value of a piece given its direction
     * Does not include logic for knights and pawns, as increments are different
     *
     * @param path the direction of interest
     * @param myPiece the chess piece in play
     * @return the row and column increment values
     */
    protected int[] setIncrements(Direction path, ChessPiece myPiece) {
        int rowIncrement;
        int colIncrement;

        if (path == Direction.UP) {
            rowIncrement = 1;
            colIncrement = 0;
        } else if (path == Direction.DOWN) {
            rowIncrement = -1;
            colIncrement = 0;
        } else if (path == Direction.LEFT) {
            rowIncrement = 0;
            colIncrement = -1;
        } else if (path == Direction.RIGHT) {
            rowIncrement = 0;
            colIncrement = 1;
        } else if (path == Direction.UP_AND_LEFT) {
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
            throw new RuntimeException(String.format("A %s cannot move in that direction!", myPiece.getPieceType()));
        }
        return new int[]{rowIncrement, colIncrement};
    }

    /**
     * Determines the moves of a piece that can only move one "space" (king, knight)
     *
     * @param board the current chess board
     * @param myPiece the chess piece in play
     * @param myPosition the current position of the piece
     * @param startRow the row the piece is on
     * @param startCol the column the piece is on
     * @param rowIncrement the value to increment the row by (indicative of the move direction)
     * @param colIncrement the value to increment the column by (indicative of the move direction)
     * @return all the possible moves of a king or knight in one direction
     */
    protected Collection<ChessMove> justOneMove(ChessBoard board, ChessPiece myPiece, ChessPosition myPosition,
                                                    int startRow, int startCol, int rowIncrement, int colIncrement) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        // Ensure potential position is on the board
        if (onBoard(startRow + rowIncrement, startCol + colIncrement)) {
            ChessPosition goodPosition = new ChessPosition(startRow + rowIncrement, startCol + colIncrement);
            // Ensure the position is not occupied by same team piece
            if (!friendlyFire(board, goodPosition, myPiece)) {
                moves.add(new ChessMove(myPosition, goodPosition, null));
            }
        }
        return moves;
    }

    /**
     * Determines the moves of a piece that can move as far as possible (queen, bishop, rook)
     *
     * @param board the current chess board
     * @param myPiece the chess piece in play
     * @param myPosition the current position of the piece
     * @param startRow the row the piece is on
     * @param startCol the column the piece is on
     * @param rowIncrement the value to increment the row by (indicative of the move direction)
     * @param colIncrement the value to increment the column by (indicative of the move direction)
     * @return all the possible moves of a queen, bishop, or rook in one direction
     */
    protected Collection<ChessMove> asFarAsPossible(ChessBoard board, ChessPiece myPiece, ChessPosition myPosition,
                                                    int startRow, int startCol, int rowIncrement, int colIncrement) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int newRow = startRow + rowIncrement;
        int newCol = startCol + colIncrement;
        // Ensure potential position is on board
        while (onBoard(newRow, newCol)) {
            ChessPosition goodPosition = new ChessPosition(newRow, newCol);
            // Check if space is occupied
            if (spaceOccupied(board, goodPosition)) {
                // Ensure the position is not occupied by same team piece
                if (!friendlyFire(board, goodPosition, myPiece)) {
                    moves.add(new ChessMove(myPosition, goodPosition, null));
                }
                break;
            }
            moves.add(new ChessMove(myPosition, goodPosition, null));
            newRow += rowIncrement;
            newCol += colIncrement;
        }
        return moves;
    }

    /**
     * Calculates all the positions a piece (other than a pawn) can move to in a single direction
     *
     * @param board the current chess board
     * @param startRow the row the rook is at
     * @param startCol the rook the queen is at
     * @param myPosition the rook's current position
     * @param myPiece the rook
     * @param path the direction of interest
     * @return collection of valid moves in a single direction
     */
    protected Collection<ChessMove> testDirection(ChessBoard board, int startRow, int startCol,
                                                  ChessPosition myPosition, ChessPiece myPiece, Direction path) {
        int rowIncrement;
        int colIncrement;

        if (myPiece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            // Determine move direction
            if (path == Direction.UP_AND_LEFT) {
                rowIncrement = 2;
                colIncrement = -1;
            } else if (path == Direction.UP_AND_RIGHT) {
                rowIncrement = 2;
                colIncrement = 1;
            } else if (path == Direction.DOWN_AND_LEFT) {
                rowIncrement = -2;
                colIncrement = -1;
            } else if (path == Direction.DOWN_AND_RIGHT) {
                rowIncrement = -2;
                colIncrement = 1;
            } else if (path == Direction.LEFT_AND_UP) {
                rowIncrement = 1;
                colIncrement = -2;
            } else if (path == Direction.LEFT_AND_DOWN) {
                rowIncrement = -1;
                colIncrement = -2;
            } else if (path == Direction.RIGHT_AND_UP) {
                rowIncrement = 1;
                colIncrement = 2;
            } else if (path == Direction.RIGHT_AND_DOWN) {
                rowIncrement = -1;
                colIncrement = 2;
            } else {
                throw new RuntimeException("A knight cannot move in that direction!");
            }
        } else {
            int[] increments = setIncrements(path, myPiece);
            rowIncrement = increments[0];
            colIncrement = increments[1];
        }

        if (myPiece.getPieceType() == ChessPiece.PieceType.KNIGHT || myPiece.getPieceType() == ChessPiece.PieceType.KING) {
            return justOneMove(board, myPiece, myPosition, startRow, startCol, rowIncrement, colIncrement);
        } else {
            return asFarAsPossible(board, myPiece, myPosition, startRow, startCol, rowIncrement, colIncrement);
        }
    }

    protected Collection<ChessMove> calculateValidMoves(Direction[] possibleDirections, ChessPiece myPiece,
                                                        ChessPosition myPosition, ChessBoard board) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        int startPositionRow = myPosition.getRow();
        int startPositionCol = myPosition.getColumn();

        for (Direction possibleDirection : possibleDirections) {
            validMoves.addAll(testDirection(board, startPositionRow,
                    startPositionCol, myPosition, myPiece, possibleDirection));
        }

        return validMoves;
    }
}
