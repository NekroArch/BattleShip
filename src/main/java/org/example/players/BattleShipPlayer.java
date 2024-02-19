package org.example.players;

import org.example.enums.ShootResult;
import org.example.event.Shootable;
import org.example.ship.Position;

import java.io.IOException;

public interface BattleShipPlayer {

    void doTurn(Shootable shootable) throws IOException;

    ShootResult checkShootAtPosition(Position position) throws IOException;
}
