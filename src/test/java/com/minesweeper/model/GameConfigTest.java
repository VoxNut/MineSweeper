package com.minesweeper.model;

import com.minesweeper.model.GameConfig;
import com.minesweeper.model.GameConfig.DifficultyConfig;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameConfigTest {

    @Test
    public void testDifficultyConfigProperties() {
        DifficultyConfig config = new DifficultyConfig();
        config.setRows(10);
        config.setCols(12);
        config.setMines(15);
        
        assertEquals(10, config.getRows());
        assertEquals(12, config.getCols());
        assertEquals(15, config.getMines());
    }

    @Test
    public void testDifficultyConfigConstructor() {
        DifficultyConfig config = new DifficultyConfig(16, 30, 99);
        
        assertEquals(16, config.getRows());
        assertEquals(30, config.getCols());
        assertEquals(99, config.getMines());
    }

    @Test
    public void testDefaultConfig() {
        GameConfig config = GameConfig.defaultConfig();
        assertNotNull(config);
        assertNotNull(config.getEasy());
        assertNotNull(config.getMedium());
        assertNotNull(config.getHard());
        
        assertEquals(9, config.getEasy().getRows());
        assertEquals(16, config.getMedium().getRows());
        assertEquals(16, config.getHard().getRows());
    }
}

// Commit: Viết unit test kiểm tra việc đọc và ghi cấu hình GameConfig | Author: Vũ Văn Long | Date: 2026-05-31 21:10:45
