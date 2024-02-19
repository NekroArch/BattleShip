package org.example.gameVersion;

import org.apache.commons.io.output.TeeOutputStream;
import org.example.board.Impl.ConstantBoardFactory;
import org.example.players.Impl.Bot;
import org.example.players.Impl.Player;
import org.example.players.exceptions.WinException;
import org.example.enums.ShipType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Scanner;

public class PlayerVsAi {
    private final Scanner scanner;
    private final String name;
    private final Map<ShipType, Integer> ships;

    public PlayerVsAi(String name, Map<ShipType, Integer> ships, Scanner scanner) {
        this.scanner = scanner;
        this.name = name;
        this.ships = ships;
    }

    public void start() throws IOException {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));

        TeeOutputStream teeOutputStream = new TeeOutputStream(new FileOutputStream(now + ".log"), System.out);

        Player player = new Player(name, ships, new ConstantBoardFactory().createBoard(), teeOutputStream);

        player.fillBoard(chooseOptionFillBoard());

        Bot bot = new Bot(ships, new ConstantBoardFactory().createBoard(), teeOutputStream);

        bot.fillBoard(true);

        teeOutputStream.write(("Start " + now + '\n').getBytes());

        while (true) {
            try {
                player.doTurn(bot);

                bot.doTurn(player);

            } catch (WinException winException) {
                saveLogs(winException, teeOutputStream, player, bot);
                teeOutputStream.write(("End " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")) + '\n').getBytes());
                break;
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

    private void saveLogs(WinException e, TeeOutputStream teeOutputStream, Player player, Bot bot) throws IOException {
        teeOutputStream.write((player.name() + '\n').getBytes());
        teeOutputStream.write(((e.getMessage().contains(player.name()) ? "win" : "lose") + '\n').getBytes());
        player.printBoards(teeOutputStream);
        teeOutputStream.write(("Ship without hits = " + player.getCountShipsWithoutHits() + '\n').getBytes());
        teeOutputStream.write(("Stroke = " + player.hodCounter() + '\n').getBytes());
        teeOutputStream.write(("Res = " + ((player.hitCounter() / player.hodCounter()) * 100) + "%" + '\n').getBytes());

        teeOutputStream.write((bot.name() + '\n').getBytes());
        teeOutputStream.write(((e.getMessage().contains(bot.name()) ? "win" : "lose") + '\n').getBytes());
        bot.printBoards(teeOutputStream);
        teeOutputStream.write(("Ship without hits = " + bot.getCountShipsWithoutHits() + '\n').getBytes());
        teeOutputStream.write(("Stroke = " + bot.hodCounter() + '\n').getBytes());
        teeOutputStream.write(("Res = " + ((bot.hitCounter() / player.hodCounter()) * 100) + "%" + '\n').getBytes());
    }
}
