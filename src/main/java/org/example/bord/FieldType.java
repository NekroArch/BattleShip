package org.example.bord;

public enum FieldType{
    WATER('~'),
    HIT('X'),
    SHIP('S'),
    MISS('.');

    private final char type;

    public char getType(){
        return type;
    }

    FieldType(char type) {
        this.type = type;
    }
}
