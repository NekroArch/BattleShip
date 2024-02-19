package org.example.gameVersion;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.enums.ShipType;
import org.example.socket.Client;
import org.example.socket.Server;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class PlayerVsPlayer {
    private final Scanner scanner;
    private final String name;
    private final Map<ShipType, Integer> ships;
    private final ObjectMapper objectMapper;

    public PlayerVsPlayer(String name, Map<ShipType, Integer> ships, ObjectMapper objectMapper, Scanner scanner) {
        this.scanner = scanner;
        this.name = name;
        this.ships = ships;
        this.objectMapper = objectMapper;
    }

    public void start() throws IOException {

        System.out.print("""
                1. Host
                2. Client
                """);
        switch (scanner.nextLine()){
            case "1" -> new Server(name, ships, objectMapper, scanner).start();
            case "2" -> new Client(name, ships, objectMapper, scanner).start();
            default -> System.out.println("Input 1 or 2");
        }

    }

}
