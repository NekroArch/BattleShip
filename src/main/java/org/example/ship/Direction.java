package org.example.ship;

public enum Direction {
    HORIZONTAL,
    VERTICAL;

    public static Direction decode(char direction) {
        if (direction == 'h' || direction == 'H'){
            return HORIZONTAL;
        }
        else if (direction == 'v' || direction == 'V'){
            return VERTICAL;
        }
        throw new RuntimeException("INPUT NORM");
    }

}
