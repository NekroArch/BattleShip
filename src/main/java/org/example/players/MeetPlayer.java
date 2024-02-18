package org.example.players;

import org.example.UserIO;
import org.example.bord.BoardFactory;
import org.example.bord.NullNumberOfShipsException;
import org.example.bord.PositionException;
import org.example.event.ShootResult;
import org.example.event.Shootable;
import org.example.ship.Direction;
import org.example.ship.Position;
import org.example.ship.Ship;
import org.example.ship.ShipType;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class MeetPlayer extends AbstractBattleShipPlayer implements Shootable, BattleShipPlayer{
    private final UserIO userIO = new UserIO(); //TODO убрать

    public MeetPlayer(String name, Map<ShipType, Integer> ships, BoardFactory board, OutputStream shootResultStream) {
        super(name, ships, board, shootResultStream);
    }

    @Override
    protected Position generateShoot() throws NumberFormatException {
        do {
            Position position = userIO.readPosition();
            if (playerBoard.positionNotOutside(position.row(), position.column())){
                return position;
            }else{
                System.out.println("POWTOR");//todo
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
    public ShootResult shoot(Position position) throws PositionException, NullNumberOfShipsException {
        return playerBoard.shoot(position);
    }

    @Override
    public void doTurn(Shootable shootable) throws IOException {

        printBoards(System.out);

        try {
            Position position = generateShoot();
            ShootResult shoot = shootable.shoot(position);

            shootResultStream.write((name + " shoot at " + position + "\n" + shoot + "\n").getBytes());

            enemyBord.setMarkOnBordByShootResult(shoot, position);

            if (shoot == ShootResult.HIT || shoot == ShootResult.DESTROY) {
                hitCounter++;
                hodCounter++;
                doTurn(shootable);
            }else{
                hodCounter++;
            }
        } catch (PositionException | NumberFormatException e) {
            doTurn(shootable);
        } catch (NullNumberOfShipsException e){
            throw new WinException(name + " win");
        }

    }

}
