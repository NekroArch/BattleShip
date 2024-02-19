package org.example.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.output.TeeOutputStream;
import org.example.board.Impl.ConstantBoardFactory;
import org.example.enums.ShootResult;
import org.example.players.Impl.Player;
import org.example.players.Impl.PlayerClient;
import org.example.ship.Position;
import org.example.enums.ShipType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Scanner;

public class Server {
    private final Scanner scanner;
    private final String name;
    private final Map<ShipType, Integer> ships;
    private final ObjectMapper objectMapper;

    public Server(String name, Map<ShipType, Integer> ships, ObjectMapper objectMapper, Scanner scanner) throws IOException {
        this.name = name;
        this.ships = ships;
        this.objectMapper = objectMapper;
        this.scanner = scanner;
    }

    public void start() throws IOException {

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));

        TeeOutputStream teeOutputStream = new TeeOutputStream(new FileOutputStream(now + ".log"), System.out);

        Player host = new Player(name, ships, new ConstantBoardFactory(), teeOutputStream);

        host.fillBoard(chooseOptionFillBoard());

        System.out.print("Input server port > ");
        int port = scanner.nextInt();

        ServerSocket serverSocket = new ServerSocket(port);

        Socket accept = serverSocket.accept();
        PlayerClient playerClient = new PlayerClient(accept, objectMapper);

        DataInputStream dataInputStream = new DataInputStream(accept.getInputStream());
        DataOutputStream dataOutputStream = new DataOutputStream(accept.getOutputStream());


        while (true) {

            try {
                host.doTurn(playerClient);

                while (true) {
                    String inputPosition = dataInputStream.readUTF();

                    Position position = objectMapper.readValue(inputPosition, Position.class);
                    ShootResult shootResult = host.checkShootAtPosition(position);

                    String outputPosition = objectMapper.writeValueAsString(shootResult);
                    dataOutputStream.writeUTF(outputPosition);
                    if (shootResult == ShootResult.MISS) {
                        break;
                    }
                }

            } catch (Exception e) {
                host.doTurn(playerClient);
            }
        }
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
}
