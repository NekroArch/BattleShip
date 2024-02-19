package org.example.enums;

import org.example.enums.Exception.DirectionException;

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
        throw new DirectionException("Input v or h");
    }

}
