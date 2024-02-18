package org.example.ship;

import java.util.Objects;

public class Position {
    private final int column;
    private final int row;

    public Position(int row, int column) {
        this.column = column;
        this.row = row;
    }
    public Position(int row, char column) {
        this.column = decode(column);
        this.row = row;
    }
    private int decode(char column) {
        return column - 'a';
    }

    @Override
    public String toString() {
        return Character.toString(((char) column + 'a')) + (row + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return column == position.column && row == position.row;
    }

    @Override
    public int hashCode() {
        return Objects.hash(column, row);
    }

    public int column() {
        return column;
    }

    public int row() {
        return row;
    }
}
