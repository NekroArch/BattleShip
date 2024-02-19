package org.example.ship;

import org.example.enums.Direction;

import java.util.ArrayList;
import java.util.List;

public class Ship {
    private final int length;
    private final Position startPosition;
    private final List<Position> positionPoint;
    private final Direction direction;

    public int health() {
        return health;
    }

    private int health;

    public Ship(int length, Position position, Direction direction) {
        this.length = length;
        this.startPosition = position;
        this.direction = direction;
        positionPoint = new ArrayList<>();
        health = length;
    }

    public List<Position> positions() {
        return positionPoint;
    }
    public int hit(){
        return health > 0 ? --health : health;
    }

    public int length() {
        return length;
    }

    public Position position() {
        return startPosition;
    }

    public boolean isVertical(){
        return direction == Direction.VERTICAL;
    }

    public boolean isHorizontal(){
        return direction == Direction.HORIZONTAL;
    }

    @Override
    public String toString() {
        return "Ship{" +
                "length=" + length +
                ", position=" + startPosition +
                ", direction=" + direction +
                ", health=" + health +
                '}';
    }
}
