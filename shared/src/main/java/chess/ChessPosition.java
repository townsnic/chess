package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 */
public class ChessPosition {

    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Overrides the equals method to compare attributes of ChessPosition
     *
     * @param obj the object to compare current instantiation to
     * @return if the objects are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null || getClass() != obj.getClass()) { return false; }
        ChessPosition pos = (ChessPosition) obj;
        return (row == pos.row && col == pos.col);
    }

    /**
     * Overrides the hash method for larger hash spread
     *
     * @return the new hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    /**
     * Overrides the toString method to print ChessPosition row and column
     *
     * @return a string of the row and column of ChessPosition
     */
    @Override
    public String toString() {
        char colChar = switch (col) {
            case 1 -> 'a';
            case 2 -> 'b';
            case 3 -> 'c';
            case 4 -> 'd';
            case 5 -> 'e';
            case 6 -> 'f';
            case 7 -> 'g';
            case 8 -> 'h';
            default -> 'x';
        };
        return String.format("%c%d", colChar, row);
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left column
     */
    public int getColumn() {
        return col;
    }
}
