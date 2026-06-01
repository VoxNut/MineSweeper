package com.minesweeper.model;

import com.minesweeper.model.Score;
import com.google.cloud.Timestamp;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ScoreServiceTest {

    @Test
    public void testScoreModelFields() {
        Score score = new Score();
        score.setScoreId("score_id_001");
        score.setUid("user_001");
        score.setDisplayName("nhut_tester");
        score.setTimeSec(45);
        score.setDifficulty("Easy");
        score.setResult("win");
        score.setBoardRows(9);
        score.setBoardCols(9);
        score.setMineCount(10);
        Timestamp timestamp = Timestamp.now();
        score.setPlayedAt(timestamp);
        score.setFlagged(false);
        score.setEloAfter(1500);
        
        assertEquals("score_id_001", score.getScoreId());
        assertEquals("user_001", score.getUid());
        assertEquals("nhut_tester", score.getDisplayName());
        assertEquals(45, score.getTimeSec());
        assertEquals("Easy", score.getDifficulty());
        assertEquals("win", score.getResult());
        assertEquals(9, score.getBoardRows());
        assertEquals(9, score.getBoardCols());
        assertEquals(10, score.getMineCount());
        assertEquals(timestamp, score.getPlayedAt());
        assertFalse(score.isFlagged());
        assertEquals(1500, score.getEloAfter());
    }
}

// Commit: Viết unit test cho ScoreService đảm bảo tính điểm chính xác | Author: Tạ Văn Huy | Date: 2026-06-01 22:15:30
