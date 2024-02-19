package org.example.event;

import org.example.enums.ShootResult;
import org.example.ship.Position;

import java.io.IOException;

@FunctionalInterface
public interface Shootable {
    ShootResult checkShootAtPosition(Position position) throws IOException;
}
