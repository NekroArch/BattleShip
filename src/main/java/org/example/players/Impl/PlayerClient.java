package org.example.players.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.enums.ShootResult;
import org.example.event.Shootable;
import org.example.players.BattleShipPlayer;
import org.example.ship.Position;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class PlayerClient implements Shootable, BattleShipPlayer {
    private final DataInputStream in;
    private final DataOutputStream out;
    private final ObjectMapper objectMapper;

    public PlayerClient(Socket socket, ObjectMapper objectMapper) throws IOException {
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        this.objectMapper = objectMapper;
    }
    @Override
    public ShootResult checkShootAtPosition(Position position) throws IOException {
        String stringPosition = objectMapper.writeValueAsString(position);
        out.writeUTF(stringPosition);
        out.flush();

        String name = in.readUTF().replaceAll("\"", "");

        return ShootResult.valueOf(name);
    }

    @Override
    public void doTurn(Shootable shootable) throws IOException {
    }
}
