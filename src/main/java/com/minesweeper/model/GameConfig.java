package com.minesweeper.model;

import com.google.cloud.Timestamp;

/**
 * Model cấu hình game cho UC-15: Cấu hình game (Độ khó).
 *
 * Thuộc tính chính:
 * - easy/medium/hard: DifficultyConfig (rows/cols/mines)
 * - updatedAt/updatedBy: metadata cho lần cập nhật gần nhất
 *
 * Fallback:
 * - defaultConfig(): cấu hình mặc định dùng khi chưa có dữ liệu trong DB.
 */
public class GameConfig {
    private DifficultyConfig easy;
    private DifficultyConfig medium;
    private DifficultyConfig hard;
    private Timestamp updatedAt;
    private String updatedBy;

    public GameConfig() {
    }

    public DifficultyConfig getEasy() {
        return easy;
    }

    public void setEasy(DifficultyConfig easy) {
        this.easy = easy;
    }

    public DifficultyConfig getMedium() {
        return medium;
    }

    public void setMedium(DifficultyConfig medium) {
        this.medium = medium;
    }

    public DifficultyConfig getHard() {
        return hard;
    }

    public void setHard(DifficultyConfig hard) {
        this.hard = hard;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public static GameConfig defaultConfig() {
        // Default theo Minesweeper cổ điển:
        // Easy: 9x9/10, Medium: 16x16/40, Hard: 16x30/99
        GameConfig config = new GameConfig();
        config.setEasy(new DifficultyConfig(9, 9, 10));
        config.setMedium(new DifficultyConfig(16, 16, 40));
        config.setHard(new DifficultyConfig(16, 30, 99));
        return config;
    }

    public static class DifficultyConfig {
        private int rows;
        private int cols;
        private int mines;

        /**
         * Cấu hình cho một mức độ khó.
         *
         * rows: số hàng
         * cols: số cột
         * mines: số mìn
         */
        public DifficultyConfig() {
        }

        public DifficultyConfig(int rows, int cols, int mines) {
            this.rows = rows;
            this.cols = cols;
            this.mines = mines;
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

        public int getMines() {
            return mines;
        }

        public void setMines(int mines) {
            this.mines = mines;
        }
    }
}
