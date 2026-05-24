package com.minesweeper.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.minesweeper.dao.ScoreDAO;
import com.minesweeper.dao.UserDAO;
import com.minesweeper.model.Board;
import com.minesweeper.model.Cell;
import com.minesweeper.model.Score;
import com.minesweeper.model.User;
import com.minesweeper.util.FirebaseUtil;

public class ScoreService {
    private static final int DEFAULT_ELO = 1000;
    private static final int K_FACTOR = 32;
    private static final int MIN_ELO = 100;
    private static final int MAX_ELO = 3000;
    private final ScoreDAO scoreDAO = new ScoreDAO();
    private final UserDAO userDAO = new UserDAO();
    private final Firestore db = FirebaseUtil.getFirestore();

    public SaveResult saveScore(String uid, String displayName, Board board, int timeSec, String result, String difficulty)
            throws ExecutionException, InterruptedException {
        Score score = new Score();
        score.setUid(uid);
        score.setDisplayName(displayName);
        score.setDifficulty(difficulty);
        score.setTimeSec(timeSec);
        score.setResult(result);
        score.setBoardRows(board.getRows());
        score.setBoardCols(board.getCols());
        score.setMineCount(board.getMineCount());
        score.setPlayedAt(Timestamp.now());
        score.setFlagged(false);
        EloUpdate eloUpdate = calculateEloUpdate(uid, result, difficulty, board);
        score.setEloAfter(eloUpdate.getAfter());
        scoreDAO.addScore(score);
        if (eloUpdate.getAfter() > 0) {
            userDAO.updateEloRating(uid, eloUpdate.getAfter());
        }

        if ("win".equalsIgnoreCase(result)) {
            updateAchievements(uid, board, timeSec, difficulty);
        }
        return new SaveResult(score, eloUpdate.getBefore(), eloUpdate.getAfter(), eloUpdate.getDelta());
    }

    private EloUpdate calculateEloUpdate(String uid, String result, String difficulty, Board board)
            throws ExecutionException, InterruptedException {
        User user = userDAO.getByUid(uid);
        int currentRating = user != null && user.getEloRating() > 0 ? user.getEloRating() : DEFAULT_ELO;
        double scoreValue = "win".equalsIgnoreCase(result) ? 1.0 : 0.0;
        int opponentRating = resolveOpponentRating(difficulty, board);
        double expected = 1.0 / (1.0 + Math.pow(10.0, (opponentRating - currentRating) / 400.0));
        int updated = (int) Math.round(currentRating + K_FACTOR * (scoreValue - expected));
        int clamped = Math.max(MIN_ELO, Math.min(MAX_ELO, updated));
        return new EloUpdate(currentRating, clamped);
    }

    public static class SaveResult {
        private final Score score;
        private final int eloBefore;
        private final int eloAfter;
        private final int eloDelta;

        public SaveResult(Score score, int eloBefore, int eloAfter, int eloDelta) {
            this.score = score;
            this.eloBefore = eloBefore;
            this.eloAfter = eloAfter;
            this.eloDelta = eloDelta;
        }

        public Score getScore() {
            return score;
        }

        public int getEloBefore() {
            return eloBefore;
        }

        public int getEloAfter() {
            return eloAfter;
        }

        public int getEloDelta() {
            return eloDelta;
        }
    }

    private static class EloUpdate {
        private final int before;
        private final int after;
        private final int delta;

        private EloUpdate(int before, int after) {
            this.before = before;
            this.after = after;
            this.delta = after - before;
        }

        private int getBefore() {
            return before;
        }

        private int getAfter() {
            return after;
        }

        private int getDelta() {
            return delta;
        }
    }

    private int resolveOpponentRating(String difficulty, Board board) {
        if (difficulty == null) {
            return DEFAULT_ELO;
        }
        String key = difficulty.toLowerCase();
        if ("easy".equals(key)) {
            return 800;
        }
        if ("medium".equals(key)) {
            return 1000;
        }
        if ("hard".equals(key)) {
            return 1200;
        }
        if ("custom".equals(key) && board != null) {
            double density = (board.getRows() * board.getCols()) > 0
                    ? board.getMineCount() / (double) (board.getRows() * board.getCols())
                    : 0.0;
            double scaled = 900 + Math.max(0.05, Math.min(0.22, density)) * 1500;
            return (int) Math.round(scaled);
        }
        return DEFAULT_ELO;
    }

    private void updateAchievements(String uid, Board board, int timeSec, String difficulty)
            throws ExecutionException, InterruptedException {
        AchievementDoc doc = loadAchievementDoc(uid);
        Set<String> unlocked = new HashSet<>(doc.getUnlocked());
        int winCount = scoreDAO.countWinsByUid(uid);
        boolean changed = false;

        if (winCount == 1 && unlocked.add("first_win")) {
            changed = true;
        }
        if (winCount >= 10 && unlocked.add("win_10")) {
            changed = true;
        }
        if ("easy".equalsIgnoreCase(difficulty) && timeSec < 30 && unlocked.add("speedrun_easy")) {
            changed = true;
        }
        if (!boardHasAnyFlag(board) && unlocked.add("no_flag_win")) {
            changed = true;
        }
        if ("hard".equalsIgnoreCase(difficulty) && unlocked.add("hard_clear")) {
            changed = true;
        }

        if (changed) {
            doc.setUnlocked(new ArrayList<>(unlocked));
            doc.setUpdatedAt(Timestamp.now());
            saveAchievementDoc(doc);
        }
    }

    private boolean boardHasAnyFlag(Board board) {
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                Cell cell = board.getCell(r, c);
                if (cell.isFlagged()) {
                    return true;
                }
            }
        }
        return false;
    }

    private AchievementDoc loadAchievementDoc(String uid) throws ExecutionException, InterruptedException {
        DocumentSnapshot snapshot = db.collection("achievements").document(uid).get().get();
        if (!snapshot.exists()) {
            return new AchievementDoc(uid, new ArrayList<>());
        }
        AchievementDoc doc = snapshot.toObject(AchievementDoc.class);
        if (doc == null) {
            return new AchievementDoc(uid, new ArrayList<>());
        }
        if (doc.getUnlocked() == null) {
            doc.setUnlocked(new ArrayList<>());
        }
        return doc;
    }

    private void saveAchievementDoc(AchievementDoc doc) throws ExecutionException, InterruptedException {
        db.collection("achievements").document(doc.getUid()).set(doc).get();
    }

    private static class AchievementDoc {
        private String uid;
        private List<String> unlocked;
        private Timestamp updatedAt;

        public AchievementDoc() {
        }

        public AchievementDoc(String uid, List<String> unlocked) {
            this.uid = uid;
            this.unlocked = unlocked;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public List<String> getUnlocked() {
            return unlocked;
        }

        public void setUnlocked(List<String> unlocked) {
            this.unlocked = unlocked;
        }

        public Timestamp getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(Timestamp updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}

/* Fixed NullPointerException when database records list is empty */
