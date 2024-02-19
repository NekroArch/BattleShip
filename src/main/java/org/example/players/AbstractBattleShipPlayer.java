package org.example.players;

import org.example.board.Impl.Board;
import org.example.board.BoardFactory;
import org.example.enums.Direction;
import org.example.ship.Position;
import org.example.ship.Ship;
import org.example.enums.ShipType;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Random;

public abstract class AbstractBattleShipPlayer {
    protected final Board playerBoard;

    protected final String name;
    protected final Board enemyBord;
    protected Map<ShipType, Integer> shipTypeToCount;
    protected static final Random random = new Random();
    protected OutputStream shootResultStream;
    protected float hitCounter;
    protected float strokeCounter;

    public float hitCounter() {
        return hitCounter;
    }
    public float hodCounter() {
        return strokeCounter;
    }
    public String name() {
        return name;
    }

    public AbstractBattleShipPlayer(String name, Map<ShipType, Integer> ships, BoardFactory board, OutputStream shootResultStream) {
        this.name = name;
        this.enemyBord = board.createBoard();
        this.playerBoard = board.createBoard();
        shipTypeToCount = ships;
        this.shootResultStream = shootResultStream;
    }

    protected abstract Position generateShoot();

    protected abstract Ship generateShip(ShipType shipType, boolean choose);

    protected Ship createShipWithRandomPosition(ShipType shipType) {
        Position position = new Position(random.nextInt(0, playerBoard.boardLength() + 1 - shipType.shipLength()), random.nextInt(0, playerBoard.boardLength() + 1 - shipType.shipLength()));
        Direction direction = random.nextBoolean() ? Direction.HORIZONTAL : Direction.VERTICAL;

        return new Ship(shipType.shipLength(), position, direction);
    }

    public void fillBoard(boolean choose) {
        shipTypeToCount.forEach((shipType, count) -> {
            for (int i = 0; i < count; i++) {
                if (!choose) {
                    try {
                        printBoards(System.out);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    System.out.println("Place ship " + shipType);
                }

                placeShip(shipType, choose);
            }
        });
    }

    private void placeShip(ShipType shipType, boolean choose) {
        try {
            Ship ship = generateShip(shipType, choose);
            playerBoard.addShip(ship);
        } catch (RuntimeException e) {
            if (!choose){
                System.out.println("Repeat");
            }
            placeShip(shipType, choose);
        }
    }

    public void printBoards(OutputStream outputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("   ");
        for (int i = 0; i < playerBoard.boardLength(); i++) {
            stringBuilder.append((char) ('A' + i)).append(" ");
        }
        stringBuilder.append("        ");
        for (int i = 0; i < enemyBord.boardLength(); i++) {
            stringBuilder.append((char) ('A' + i)).append(" ");
        }
        stringBuilder.append("\n");
        for (int i = 0; i < playerBoard.boardLength(); i++) {
            stringBuilder.append(" ").append(i + 1).append(" ");
            for (int j = 0; j < playerBoard.boardLength(); j++) {
                stringBuilder.append(playerBoard.board()[i][j]).append(" ");
            }
            stringBuilder.append("     ");
            stringBuilder.append(" ").append(i + 1).append(" ");
            for (int j = 0; j < enemyBord.boardLength(); j++) {
                stringBuilder.append(enemyBord.board()[i][j]).append(" ");
            }
            stringBuilder.append("\n");
        }

        outputStream.write(stringBuilder.toString().getBytes());
    }

    public long getCountShipsWithoutHits(){
       return playerBoard.getCountShipsWithoutHits();
    }

}
