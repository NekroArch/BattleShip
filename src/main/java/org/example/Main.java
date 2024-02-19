package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.gameVersion.PlayerVsPlayer;
import org.example.gameVersion.PlayerVsAi;
import org.example.enums.ShipType;

import java.io.*;
import java.util.*;


public class Main {


    public static void main(String[] args) throws IOException {

        Map<ShipType, Integer> ships = new LinkedHashMap<>() {{
            put(ShipType.SIX, 1);
            put(ShipType.FIVE, 2);
            put(ShipType.FOUR, 3);
            put(ShipType.THREE, 4);
            put(ShipType.TWO, 5);
            put(ShipType.ONE, 6);
        }};

        ObjectMapper objectMapper = new ObjectMapper();

        Scanner scanner = new Scanner(System.in);

        boolean flag = true;

        String userName;

        do {
            System.out.print("Input name > ");
            userName = scanner.nextLine();
        } while (userName.isEmpty());

        while (flag) {

            System.out.print("""
                    1. Bot
                    2. PVP
                    3. Exit
                    """);

            switch (scanner.nextLine()) {
                case "1" -> new PlayerVsAi(userName, ships, scanner).start();
                case "2" -> new PlayerVsPlayer(userName, ships, objectMapper, scanner).start();
                case "3" -> flag = false;
                default -> System.out.println("Input 1, 2 or 3");
            }
        }
    }
}

