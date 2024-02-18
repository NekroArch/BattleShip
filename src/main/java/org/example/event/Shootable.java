package org.example.event;

import org.example.ship.Position;

@FunctionalInterface
public interface Shootable {
    ShootResult shoot(Position position);
}
