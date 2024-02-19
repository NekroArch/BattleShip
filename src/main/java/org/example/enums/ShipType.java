package org.example.enums;

public enum ShipType {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6);
    private final int shipLength;

    ShipType(int shipLength) {
        this.shipLength = shipLength;
    }

    public int shipLength() {
        return shipLength;
    }
}
