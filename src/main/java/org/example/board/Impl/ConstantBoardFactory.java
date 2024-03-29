package org.example.board.Impl;

import org.example.board.BoardFactory;

public class ConstantBoardFactory implements BoardFactory {
    private static final int BOARD_LENGTH = 16;

    @Override
    public Board createBoard() {
        return new Board(BOARD_LENGTH);
    }
}
