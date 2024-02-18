package org.example.players;

import org.example.event.Shootable;

import java.io.IOException;

public interface BattleShipPlayer {

    // расставить, выстрелить, проверить чужой выстрел, увидеть свою борду

    void doTurn(Shootable shootable) throws IOException;
}
