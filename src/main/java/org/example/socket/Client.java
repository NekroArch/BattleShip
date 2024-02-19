package org.example.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.board.Impl.ConstantBoardFactory;
import org.example.enums.ShipType;
import org.example.enums.ShootResult;
import org.example.players.Impl.Player;
import org.example.players.Impl.PlayerClient;
import org.example.ship.Position;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;

public class Client {
    private final Scanner scanner;
    private final String name;
    private final Map<ShipType, Integer> ships;
    private final ObjectMapper objectMapper;

    public Client(String name, Map<ShipType, Integer> ships, ObjectMapper objectMapper, Scanner scanner) throws IOException {
        this.name = name;
        this.ships = ships;
        this.objectMapper = objectMapper;
        this.scanner = scanner;
    }

    public void start() throws IOException {

        Player client = new Player(name, ships, new ConstantBoardFactory(), System.out);

        client.fillBoard(chooseOptionFillBoard());

        Result result = inputHostIpAndPort();

        Socket socket = new Socket(result.hostIp(), result.port());

        PlayerClient playerClient = new PlayerClient(socket, objectMapper);

        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

        while (true) {

            try {
                while (true) {
                    String inputPosition = dataInputStream.readUTF();

                    Position position = objectMapper.readValue(inputPosition, Position.class);
                    ShootResult shootResult = client.checkShootAtPosition(position);

                    String outputPosition = objectMapper.writeValueAsString(shootResult);
                    dataOutputStream.writeUTF(outputPosition);

                    if (shootResult == ShootResult.MISS) {
                        break;
                    }
                }
                client.doTurn(playerClient);
            } catch (Exception e) {
                client.doTurn(playerClient);
            }
        }
    }

    private Result inputHostIpAndPort() {
        System.out.print("Input host ip > ");
        String hostIp = scanner.nextLine();

        System.out.print("Input port > ");
        int port = scanner.nextInt();
        System.out.println();

        return new Result(hostIp, port);
    }

    private boolean chooseOptionFillBoard() {
        boolean chooseFlag = true;
        boolean choose = true;
        System.out.println("""
                1. Auto
                2. Hand
                """);
        while (chooseFlag) {

            switch (scanner.nextLine()) {
                case "1" -> {
                    choose = true;
                    chooseFlag = false;
                }
                case "2" -> {
                    choose = false;
                    chooseFlag = false;
                }
                default -> System.out.println("1. 2.");
            }
        }
        return choose;
    }

    private record Result(String hostIp, int port) {
    }
}

