package com.minesweeper.model;

public class Board {
    private int rows;
    private int cols;
    private int mineCount;
    private boolean minesPlaced;
    private Cell[][] cells;

    public Board() {
    }

    public Board(int rows, int cols, int mineCount) {
        this.rows = rows;
        this.cols = cols;
        this.mineCount = mineCount;
        this.minesPlaced = true;
        this.cells = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                this.cells[r][c] = new Cell();
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public int getMineCount() {
        return mineCount;
    }

    public void setMineCount(int mineCount) {
        this.mineCount = mineCount;
    }

    public boolean isMinesPlaced() {
        return minesPlaced;
    }

    public void setMinesPlaced(boolean minesPlaced) {
        this.minesPlaced = minesPlaced;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }

    public Cell getCell(int row, int col) {
        return cells[row][col];
    }
}
