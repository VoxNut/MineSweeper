package com.minesweeper.model;

import com.minesweeper.model.Board;
import com.minesweeper.model.Cell;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    @Test
    public void testBoardInitialization() {
        int rows = 9;
        int cols = 9;
        int mines = 10;
        Board board = new Board(rows, cols, mines);
        
        assertEquals(rows, board.getRows(), "Row count mismatch");
        assertEquals(cols, board.getCols(), "Col count mismatch");
        assertEquals(mines, board.getMineCount(), "Mine count mismatch");
        assertNotNull(board.getCells(), "Grid should not be null");
    }

    @Test
    public void testCellDefaultState() {
        Board board = new Board(9, 9, 10);
        Cell[][] cells = board.getCells();
        
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Cell cell = cells[i][j];
                assertNotNull(cell, "Cell should be initialized");
                assertFalse(cell.isRevealed(), "Cell should not be revealed initially");
                assertFalse(cell.isFlagged(), "Cell should not be flagged initially");
                assertFalse(cell.isMine(), "Cell should not be mine initially before generation");
            }
        }
    }
}
