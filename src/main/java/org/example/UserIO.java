package org.example;

import org.example.bord.Board;
import org.example.ship.Direction;
import org.example.ship.Position;

import java.util.Scanner;

public class UserIO {
    private final Scanner scanner = new Scanner(System.in);

    public Position readPosition() {
        System.out.println("Position");
        String upperCase = scanner.nextLine().toLowerCase();
        char column = upperCase.charAt(0);
        int row = Integer.parseInt(upperCase.substring(1));

        return new Position(row - 1, column);
    }

    public Direction readDirection() {
        System.out.println("Direction");
        String upperCase = scanner.nextLine();

        return Direction.decode(upperCase.charAt(0));
    }
}
