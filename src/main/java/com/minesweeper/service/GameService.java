package com.minesweeper.service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import javax.servlet.ServletContext;

import com.minesweeper.dao.GameConfigDAO;
import com.minesweeper.model.Board;
import com.minesweeper.model.Cell;
import com.minesweeper.model.GameConfig;

public class GameService {
    private static final String CONFIG_CACHE_KEY = "gameConfigCache";
    private static final String CONFIG_CACHE_TIME_KEY = "gameConfigCacheTime";
    private static final long CONFIG_CACHE_TTL_MS = 5 * 60 * 1000L;

    private final Random random = new Random();
    private final GameConfigDAO gameConfigDAO = new GameConfigDAO();

    public Board createEmptyBoard(int rows, int cols, int mines) {
        Board board = new Board(rows, cols, mines);
        board.setMinesPlaced(false);
        return board;
    }

    public int resolveRows(ServletContext context, String difficulty, Integer customRows) throws Exception {
        GameConfig.DifficultyConfig config = resolveDifficultyConfig(context, difficulty);
        if (config != null && customRows == null) {
            return config.getRows();
        }
        return customRows != null ? customRows : fallbackRows(difficulty);
    }

    public int resolveCols(ServletContext context, String difficulty, Integer customCols) throws Exception {
        GameConfig.DifficultyConfig config = resolveDifficultyConfig(context, difficulty);
        if (config != null && customCols == null) {
            return config.getCols();
        }
        return customCols != null ? customCols : fallbackCols(difficulty);
    }

    public int resolveMines(ServletContext context, String difficulty, Integer customMines) throws Exception {
        GameConfig.DifficultyConfig config = resolveDifficultyConfig(context, difficulty);
        if (config != null && customMines == null) {
            return config.getMines();
        }
        return customMines != null ? customMines : fallbackMines(difficulty);
    }

    private GameConfig.DifficultyConfig resolveDifficultyConfig(ServletContext context, String difficulty) throws Exception {
        if (difficulty == null) {
            return null;
        }
        String normalized = difficulty.toLowerCase();
        if (!"easy".equals(normalized) && !"medium".equals(normalized) && !"hard".equals(normalized)) {
            return null;
        }

        GameConfig config = getCachedConfig(context);
        if (config == null) {
            gameConfigDAO.ensureDefaultConfig();
            config = gameConfigDAO.getDefaultConfig();
            cacheConfig(context, config);
        }

        if ("medium".equals(normalized)) {
            return config.getMedium();
        }
        if ("hard".equals(normalized)) {
            return config.getHard();
        }
        return config.getEasy();
    }

    private GameConfig getCachedConfig(ServletContext context) {
        if (context == null) {
            return null;
        }
        Object cached = context.getAttribute(CONFIG_CACHE_KEY);
        Object cachedAt = context.getAttribute(CONFIG_CACHE_TIME_KEY);
        if (cached instanceof GameConfig && cachedAt instanceof Long) {
            long age = System.currentTimeMillis() - (Long) cachedAt;
            if (age <= CONFIG_CACHE_TTL_MS) {
                return (GameConfig) cached;
            }
        }
        return null;
    }

    private void cacheConfig(ServletContext context, GameConfig config) {
        if (context == null || config == null) {
            return;
        }
        context.setAttribute(CONFIG_CACHE_KEY, config);
        context.setAttribute(CONFIG_CACHE_TIME_KEY, System.currentTimeMillis());
    }

    private int fallbackRows(String difficulty) {
        if ("medium".equalsIgnoreCase(difficulty) || "hard".equalsIgnoreCase(difficulty)) {
            return 16;
        }
        return 9;
    }

    private int fallbackCols(String difficulty) {
        if ("hard".equalsIgnoreCase(difficulty)) {
            return 30;
        }
        if ("medium".equalsIgnoreCase(difficulty)) {
            return 16;
        }
        return 9;
    }

    private int fallbackMines(String difficulty) {
        if ("hard".equalsIgnoreCase(difficulty)) {
            return 99;
        }
        if ("medium".equalsIgnoreCase(difficulty)) {
            return 40;
        }
        return 10;
    }

    public Board generateBoard(int rows, int cols, int mines, int firstClickRow, int firstClickCol) {
        Board board = new Board(rows, cols, mines);
        int safeIndex = firstClickRow * cols + firstClickCol;
        int totalCells = rows * cols;
        int available = totalCells - 1;
        int minesToPlace = Math.min(mines, available);
        List<Integer> positions = new ArrayList<>(available);
        for (int i = 0; i < totalCells; i++) {
            if (i != safeIndex) {
                positions.add(i);
            }
        }
        Collections.shuffle(positions, random);
        for (int i = 0; i < minesToPlace; i++) {
            int pos = positions.get(i);
            int r = pos / cols;
            int c = pos % cols;
            board.getCell(r, c).setMine(true);
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = board.getCell(r, c);
                if (cell.isMine()) {
                    continue;
                }
                int count = countAdjacentMines(board, r, c);
                cell.setAdjacentMines(count);
            }
        }
        board.setMinesPlaced(true);
        return board;
    }

    public RevealResult revealCell(Board board, int row, int col) {
        Cell cell = board.getCell(row, col);
        if (cell.isRevealed() || cell.isFlagged()) {
            return new RevealResult("CONTINUE", new ArrayList<>());
        }
        if (cell.isMine()) {
            cell.setRevealed(true);
            List<CellUpdate> updates = new ArrayList<>();
            updates.add(new CellUpdate(row, col, cell.getAdjacentMines(), cell.isMine(), cell.isFlagged(), cell.isRevealed()));
            return new RevealResult("LOSE", updates);
        }

        List<CellUpdate> updates = new ArrayList<>();
        if (cell.getAdjacentMines() > 0) {
            cell.setRevealed(true);
            updates.add(new CellUpdate(row, col, cell.getAdjacentMines(), false, cell.isFlagged(), true));
        } else {
            floodReveal(board, row, col, updates);
        }

        String status = checkWin(board) ? "WIN" : "CONTINUE";
        return new RevealResult(status, updates);
    }

    private void floodReveal(Board board, int startRow, int startCol, List<CellUpdate> updates) {
        int rows = board.getRows();
        int cols = board.getCols();
        boolean[][] visited = new boolean[rows][cols];
        Queue<int[]> queue = new ArrayDeque<>();
        queue.add(new int[]{startRow, startCol});

        while (!queue.isEmpty()) {
            int[] pos = queue.poll();
            int r = pos[0];
            int c = pos[1];
            if (r < 0 || r >= rows || c < 0 || c >= cols) {
                continue;
            }
            if (visited[r][c]) {
                continue;
            }
            Cell cell = board.getCell(r, c);
            if (cell.isFlagged() || cell.isMine()) {
                continue;
            }
            visited[r][c] = true;
            cell.setRevealed(true);
            updates.add(new CellUpdate(r, c, cell.getAdjacentMines(), false, cell.isFlagged(), true));

            if (cell.getAdjacentMines() == 0) {
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        if (dr == 0 && dc == 0) {
                            continue;
                        }
                        queue.add(new int[]{r + dr, c + dc});
                    }
                }
            }
        }
    }

    private int countAdjacentMines(Board board, int row, int col) {
        int count = 0;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) {
                    continue;
                }
                int nr = row + dr;
                int nc = col + dc;
                if (nr < 0 || nr >= board.getRows() || nc < 0 || nc >= board.getCols()) {
                    continue;
                }
                if (board.getCell(nr, nc).isMine()) {
                    count++;
                }
            }
        }
        return count;
    }

    public boolean checkWin(Board board) {
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                Cell cell = board.getCell(r, c);
                if (!cell.isMine() && !cell.isRevealed()) {
                    return false;
                }
            }
        }
        return true;
    }

    public static class RevealResult {
        private final String status;
        private final List<CellUpdate> revealedCells;

        public RevealResult(String status, List<CellUpdate> revealedCells) {
            this.status = status;
            this.revealedCells = revealedCells;
        }

        public String getStatus() {
            return status;
        }

        public List<CellUpdate> getRevealedCells() {
            return revealedCells;
        }
    }

    public static class CellUpdate {
        private int row;
        private int col;
        private int adjacentMines;
        private boolean isMine;
        private boolean isFlagged;
        private boolean isRevealed;

        public CellUpdate() {
        }

        public CellUpdate(int row, int col, int adjacentMines, boolean isMine, boolean isFlagged, boolean isRevealed) {
            this.row = row;
            this.col = col;
            this.adjacentMines = adjacentMines;
            this.isMine = isMine;
            this.isFlagged = isFlagged;
            this.isRevealed = isRevealed;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public int getCol() {
            return col;
        }

        public void setCol(int col) {
            this.col = col;
        }

        public int getAdjacentMines() {
            return adjacentMines;
        }

        public void setAdjacentMines(int adjacentMines) {
            this.adjacentMines = adjacentMines;
        }

        public boolean isMine() {
            return isMine;
        }

        public void setMine(boolean mine) {
            isMine = mine;
        }

        public boolean isFlagged() {
            return isFlagged;
        }

        public void setFlagged(boolean flagged) {
            isFlagged = flagged;
        }

        public boolean isRevealed() {
            return isRevealed;
        }

        public void setRevealed(boolean revealed) {
            isRevealed = revealed;
        }
    }
}
