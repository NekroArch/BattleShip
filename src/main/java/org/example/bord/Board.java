package org.example.bord;

import org.example.event.ShootResult;
import org.example.ship.Position;
import org.example.ship.Ship;

import java.util.*;
import java.util.List;

public class Board extends ConstantBoardFactory {
    public char[][] board() {
        return board;
    }

    private char[][] board;
    private final int boardLength;
    //todo подумать над шизой
    private final List<Ship> ships;

    public Board(int boardLength) {
        this.boardLength = boardLength;
        ships = new ArrayList<>();
        fillBoard(boardLength);
    }

    private void fillBoard(int boardLength) {
        board = new char[boardLength][boardLength];
        for (char[] row : board) {
            Arrays.fill(row, FieldType.WATER.getType());
        }
    }

    public void addShip(Ship ship) {
        if (closeCellsAreEmpty(ship)) {
            for (int i = 0; i < ship.length(); i++) {
                if (ship.isHorizontal()) {
                    board[ship.position().row()][ship.position().column() + i] = FieldType.SHIP.getType();
                    ship.positions().add(new Position(ship.position().row(), ship.position().column() + i));
                } else {
                    board[ship.position().row() + i][ship.position().column()] = FieldType.SHIP.getType();
                    ship.positions().add(new Position(ship.position().row() + i, ship.position().column()));
                }
            }
            ships.add(ship);
        } else {
            throw new PositionException("nIza");//todo
        }
    }

    private boolean closeCellsAreEmpty(Ship ship) {
        int[] xOffset = {-1, 0, 1, -1, 1, -1, 0, 1, 0};
        int[] yOffset = {-1, -1, -1, 0, 0, 1, 1, 1, 0};

        for (int i = 0; i < ship.length(); i++) {
            for (int j = 0; j < xOffset.length; j++) {

                int newRow = ship.position().row() + (ship.isHorizontal() ? 0 : i) + xOffset[j];
                int newColumn = ship.position().column() + (ship.isHorizontal() ? i : 0) + yOffset[j];

                if (positionNotOutside(newRow, newColumn) && board[newRow][newColumn] == FieldType.SHIP.getType()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean positionNotOutside(int newRow, int newColumn) {
        return newRow >= 0 && newColumn >= 0 && newRow < boardLength && newColumn < boardLength;
    }

    public ShootResult shoot(Position position) {
        if (positionNotOutside(position.row(), position.column())) {
            if (board[position.row()][position.column()] == FieldType.SHIP.getType()) {

                board[position.row()][position.column()] = FieldType.HIT.getType();

                Optional<Ship> shipByPosition = ships.stream()
                        .filter(ship -> ship.positions().contains(position))
                        .findFirst();

                if (shipByPosition.get().hit() == 0) {
                    markAroundDestroyedShip(shipByPosition.get());

                    if (ships.stream().allMatch(ship -> ship.health() == 0)){
                        throw new NullNumberOfShipsException();
                    }

                    return ShootResult.DESTROY;
                }

                return ShootResult.HIT;

            } else if (board[position.row()][position.column()] == FieldType.WATER.getType()) {

                board[position.row()][position.column()] = FieldType.MISS.getType();
                return ShootResult.MISS;

            } else {
                throw new PositionException("Niza");//todo
            }
        }
        throw new PositionException("Position out of bound");
    }

    public int boardLength() {
        return boardLength;
    }

    //todo
    private void markAroundDestroyedShip(Ship ship) {

        int[] xOffset = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] yOffset = {-1, -1, -1, 0, 0, 1, 1, 1};

        ship.positions().forEach(position -> {
            for (int i = 0; i < xOffset.length; i++) {
                int newRow = position.row() + xOffset[i];
                int newColumn = position.column() + yOffset[i];
                if (positionNotOutside(newRow, newColumn) && board[newRow][newColumn] == FieldType.WATER.getType()) {
                    board[newRow][newColumn] = FieldType.MISS.getType();
                }
            }
        });
    }


    public void setMarkOnBordByShootResult(ShootResult shootResult, Position position) {
        switch (shootResult){
            case HIT -> board[position.row()][position.column()] = FieldType.HIT.getType();
            case MISS -> board[position.row()][position.column()] = FieldType.MISS.getType();
            case DESTROY -> {
                board[position.row()][position.column()] = FieldType.HIT.getType();
                markAroundDestroyedEnemyShip(position);
            }
        }
    }

    private int findShipLength(int x, int y) {
        int[] xOffset = {-1, 0, 1, 0};
        int[] yOffset = {0, 1, 0, -1};

        int counter = 1;

        for (int i = 0; i < xOffset.length; i++) {

            int newRow = x + xOffset[i];
            int newColumn = y + yOffset[i];

            while (newRow >= 0 && newColumn >= 0 && newRow < boardLength && newColumn < boardLength && board[newRow][newColumn] == FieldType.HIT.getType()) {
                newRow += xOffset[i];
                newColumn += yOffset[i];
                counter++;
            }
        }

        return counter;
    }

    //todo
    private void markAroundDestroyedEnemyShip(Position position) {

        int[] xOffset = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] yOffset = {-1, -1, -1, 0, 0, 1, 1, 1};

        int shipLength = findShipLength(position.row(), position.column());
        List<Position> save = new ArrayList<>();
        save.add(position);

        for (int i = 0; i < shipLength; i++) {
            int x = save.get(i).row();
            int y = save.get(i).column();
            for (int j = 0; j < xOffset.length; j++) {
                int newRow = x + xOffset[j];
                int newColumn = y + yOffset[j];

                if (positionNotOutside(newRow, newColumn) && board[newRow][newColumn] == FieldType.HIT.getType() && !save.contains(new Position(newRow, newColumn))) {
                    save.add(new Position(newRow, newColumn));
                } else if (positionNotOutside(newRow, newColumn) && board[newRow][newColumn] == FieldType.WATER.getType()) {
                    board[newRow][newColumn] = FieldType.MISS.getType();
                }
            }
        }
    }

    public long getCountShipsWithoutHits() {
        return ships.stream().filter(ship -> ship.health() == ship.length()).count();
    }
}

