package com.minesweeper.service;

import com.minesweeper.service.GameService;
import com.minesweeper.model.Board;
import com.minesweeper.model.Cell;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private final GameService gameService = new GameService();

    @Test
    public void testCreateEmptyBoard() {
        Board board = gameService.createEmptyBoard(10, 10, 15);
        assertNotNull(board);
        assertEquals(10, board.getRows());
        assertEquals(10, board.getCols());
        assertEquals(15, board.getMineCount());
        
        Cell[][] cells = board.getCells();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                assertFalse(cells[i][j].isMine());
            }
        }
    }
}

// Commit: Viết unit test cho GameService kiểm tra logic rải mìn | Author: Nguyễn Đức Khải | Date: 2026-05-31 10:15:30
