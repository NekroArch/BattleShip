package org.example.players.Impl;

import org.example.board.*;
import org.example.board.exceptions.NullShipsException;
import org.example.board.exceptions.PositionException;
import org.example.enums.FieldType;
import org.example.enums.ShootResult;
import org.example.event.Shootable;
import org.example.players.AbstractBattleShipPlayer;
import org.example.players.BattleShipPlayer;
import org.example.players.exceptions.WinException;
import org.example.ship.Position;
import org.example.ship.Ship;
import org.example.enums.ShipType;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class Bot extends AbstractBattleShipPlayer implements Shootable, BattleShipPlayer {
    private int lastHitX = -1;
    private int lastHitY = -1;
    private int firstHitX = -1;
    private int firstHitY = -1;
    private int currentDirection = 0;

    public Bot(Map<ShipType, Integer> ships, BoardFactory board, OutputStream shootResultStream) {
        super("Bot", ships, board, shootResultStream);
    }

    private Position findPositionByLastHit(int lastHitX, int lastHitY) {
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        int[] dir = dirs[currentDirection];

        return new Position(lastHitX + dir[0],
                lastHitY + dir[1]);
    }

    public void recordShotResult(Position position, ShootResult shootResult) {
        if (shootResult == ShootResult.DESTROY) {
            resetAfterSunk();
        }
        if (shootResult == ShootResult.HIT) {
            if (firstHitX == -1 && firstHitY == -1) {
                firstHitX = position.row();
                firstHitY = position.column();
            }
            lastHitX = position.row();
            lastHitY = position.column();
        } else {
            if (lastHitX != -1 && lastHitY != -1) {
                lastHitX = firstHitX;
                lastHitY = firstHitY;
                currentDirection = (currentDirection + 1) % 4;
            }
        }
    }

    private void resetAfterSunk() {
        firstHitX = -1;
        firstHitY = -1;
        lastHitX = -1;
        lastHitY = -1;
        currentDirection = 0;
    }

    @Override
    protected Ship generateShip(ShipType shipType, boolean choose) {
        return createShipWithRandomPosition(shipType);
    }

    @Override
    public ShootResult checkShootAtPosition(Position position) throws PositionException, NullShipsException {
        return playerBoard.shoot(position);
    }

    @Override
    public void doTurn(Shootable shootable) throws IOException {
        try {
            Position position = generateShoot();
            ShootResult shoot = shootable.checkShootAtPosition(position);

            shootResultStream.write((name + " shoot at " + position + "\n" + shoot + "\n").getBytes());

            enemyBord.setMarkOnBordByShootResult(shoot, position);

            strokeCounter++;
            if (shoot == ShootResult.HIT || shoot == ShootResult.DESTROY) {
                hitCounter++;
                recordShotResult(position, shoot);
                doTurn(shootable);
            }
        } catch (PositionException e) {
            doTurn(shootable);
        } catch (ArrayIndexOutOfBoundsException e){
            currentDirection = (currentDirection + 1) % 4;
        } catch (NullShipsException e){
            throw new WinException(name + " win");
        }
    }

    @Override
    protected Position generateShoot() throws ArrayIndexOutOfBoundsException {
        Position position;

        if (firstHitX != -1 && firstHitY != -1) {
            position = findPositionByLastHit(lastHitX, lastHitY);
            if (hasBeenHit(position)) {

                lastHitX = firstHitX;
                lastHitY = firstHitY;
                currentDirection = (currentDirection + 1) % 4;
                position = findPositionByLastHit(lastHitX, lastHitY);
            }

        } else {
            position = new Position(random.nextInt(0, enemyBord.boardLength()), random.nextInt(0, enemyBord.boardLength()));
        }

        return position;
    }

    private boolean hasBeenHit(Position position) {
        return enemyBord.board()[position.row()][position.column()] == FieldType.HIT.getType() || enemyBord.board()[position.row()][position.column()] == FieldType.MISS.getType();
    }
}
