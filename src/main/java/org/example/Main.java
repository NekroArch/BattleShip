package org.example;

import org.apache.commons.io.output.TeeOutputStream;
import org.example.bord.ConstantBoardFactory;
import org.example.players.Bot;
import org.example.players.MeetPlayer;
import org.example.players.WinException;
import org.example.ship.ShipType;

import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class Main {
    static Map<ShipType, Integer> six = new LinkedHashMap<>() {{
        put(ShipType.SIX, 1);
        put(ShipType.FIVE, 2);
        put(ShipType.FOUR, 3);
        put(ShipType.THREE, 4);
        put(ShipType.TWO, 5);
        put(ShipType.ONE, 6);
    }};


    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);

        TeeOutputStream teeOutputStream;

        boolean flag = true;

        String s;

        do {
            System.out.print("Input name > ");
            s = scanner.nextLine();
        } while (s.isEmpty());

        while (flag) {

            System.out.println("""
                    1. Bot
                    2. PVP
                    3. Exit
                    """);

            switch (scanner.nextLine()) {
                case "1" -> {

                    String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));

                    teeOutputStream = new TeeOutputStream(new FileOutputStream(now + ".log"), System.out);

                    MeetPlayer player = new MeetPlayer(s, six, new ConstantBoardFactory().createBoard(), teeOutputStream);

                    boolean chooseFlag = true;
                    boolean choose = true;
                    do {
                        System.out.println("""
                                1. Auto
                                2. Hand
                                """);

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
                    } while (chooseFlag);

                    player.fillBoard(choose);

                    Bot bot = new Bot(six, new ConstantBoardFactory().createBoard(), teeOutputStream);

                    bot.fillBoard(true);

                    teeOutputStream.write(("Start " + now + '\n').getBytes());


                    while (true) {
                        try {
                            player.doTurn(bot);

                            bot.doTurn(player);

                        } catch (WinException e) {
//todo
                            teeOutputStream.write((player.name()+ '\n').getBytes());
                            teeOutputStream.write(((e.getMessage().contains(player.name()) ? "win" : "lose") + '\n').getBytes());
                            player.printBoards(teeOutputStream);
                            teeOutputStream.write(("Ship without hits = " + player.getCountShipsWithoutHits()+ '\n').getBytes());
                            teeOutputStream.write(("Hods = " + player.hodCounter()+ '\n').getBytes());
                            teeOutputStream.write(("Res = " + ((player.hitCounter()/player.hodCounter())*100) + "%"+ '\n').getBytes());

                            teeOutputStream.write((bot.name()+ '\n').getBytes());
                            teeOutputStream.write(((e.getMessage().contains(bot.name()) ? "win" : "lose") + '\n').getBytes());
                            bot.printBoards(teeOutputStream);
                            teeOutputStream.write(("Ship without hits = " + bot.getCountShipsWithoutHits()+ '\n').getBytes());
                            teeOutputStream.write(("Hods = " + bot.hodCounter()+ '\n').getBytes());
                            teeOutputStream.write(("Res = " + ((bot.hitCounter()/player.hodCounter())*100) + "%"+ '\n').getBytes());

                            teeOutputStream.write(("End " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")) + '\n').getBytes());

                            break;
                        }
                    }
                }
                case "2" -> System.out.println("2");
                case "3" -> flag = false;
                default -> System.out.println("1. 2. 3.");
            }

        }
    }
}

