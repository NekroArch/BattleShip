package org.example.players.Impl;

import org.example.players.exceptions.WinException;
import org.example.userIO.UserInput;
import org.example.board.BoardFactory;
import org.example.board.exceptions.NullShipsException;
import org.example.board.exceptions.PositionException;
import org.example.enums.Direction;
import org.example.enums.ShipType;
import org.example.enums.ShootResult;
import org.example.event.Shootable;
import org.example.players.AbstractBattleShipPlayer;
import org.example.players.BattleShipPlayer;
import org.example.ship.Position;
import org.example.ship.Ship;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class Player extends AbstractBattleShipPlayer implements Shootable, BattleShipPlayer {
    private final UserInput userIO;

    public Player(String name, Map<ShipType, Integer> ships, BoardFactory board, OutputStream shootResultStream) {
        super(name, ships, board, shootResultStream);
        userIO = new UserInput();
    }

    @Override
    protected Position generateShoot() throws NumberFormatException {
        do {
            Position position = userIO.readPosition();
            if (playerBoard.positionNotOutside(position.row(), position.column())) {
                return position;
            } else {
                System.out.println("Repeat");
            }
        } while (true);
    }

    @Override
    protected Ship generateShip(ShipType shipType, boolean choose) {
        Ship ship;

        if (choose) {
            ship = createShipWithRandomPosition(shipType);
        } else {
            ship = createShip(shipType);
        }
        return ship;
    }

    private Ship createShip(ShipType shipType) {
        Position position = userIO.readPosition();
        Direction direction = userIO.readDirection();

        return new Ship(shipType.shipLength(), position, direction);
    }

    @Override
    public ShootResult checkShootAtPosition(Position position) throws PositionException, NullShipsException {
        return playerBoard.shoot(position);
    }

    @Override
    public void doTurn(Shootable shootable) throws IOException, NullShipsException{

        printBoards(System.out);

        try {
            Position position = generateShoot();
            ShootResult shoot = shootable.checkShootAtPosition(position);

            shootResultStream.write((name + " shoot at " + position + "\n" + shoot + "\n").getBytes());

            enemyBord.setMarkOnBordByShootResult(shoot, position);

            strokeCounter++;
            if (shoot == ShootResult.HIT || shoot == ShootResult.DESTROY) {
                hitCounter++;

                doTurn(shootable);
            }
        } catch (PositionException | NumberFormatException e) {
            doTurn(shootable);
        } catch (NullShipsException e){
            throw new WinException(name + " win");
        }
    }

}
