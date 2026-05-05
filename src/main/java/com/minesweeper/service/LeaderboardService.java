package com.minesweeper.service;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.minesweeper.dao.ScoreDAO;
import com.minesweeper.model.Score;

public class LeaderboardService {
    private final ScoreDAO scoreDAO = new ScoreDAO();

    public List<Score> getTopScores(String difficulty) throws ExecutionException, InterruptedException {
        return scoreDAO.getTopScores(difficulty, 20);
    }

    public List<Score> getTopScores(String difficulty, int limit) throws ExecutionException, InterruptedException {
        return scoreDAO.getTopScores(difficulty, limit);
    }

    public List<Score> getHistory(String uid) throws ExecutionException, InterruptedException {
        return scoreDAO.getScoresByUid(uid);
    }

    public List<Score> getRecentScores(int limit) throws ExecutionException, InterruptedException {
        return scoreDAO.getRecentScores(limit);
    }
}
