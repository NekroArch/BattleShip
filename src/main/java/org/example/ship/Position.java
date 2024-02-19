package org.example.ship;

import java.util.Objects;

public record Position(int row, int column) {

    public Position(int row, char column) {
        this(row, decode(column));
    }

    private static int decode(char column) {
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
}
