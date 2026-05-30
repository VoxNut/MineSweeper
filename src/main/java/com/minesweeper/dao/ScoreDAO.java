package com.minesweeper.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.minesweeper.model.Score;
import com.minesweeper.util.FirebaseUtil;

public class ScoreDAO {
    private final Firestore db;

    public ScoreDAO() {
        this.db = FirebaseUtil.getFirestore();
    }

    public String addScore(Score score) throws ExecutionException, InterruptedException {
        DocumentReference ref = db.collection("scores").document();
        score.setScoreId(ref.getId());
        ref.set(score).get();
        return ref.getId();
    }

    public List<Score> getTopScores(String difficulty, int limit) throws ExecutionException, InterruptedException {
        Query query = db.collection("scores")
                .whereEqualTo("result", "win")
                .whereEqualTo("flagged", false);
        if (difficulty != null && !difficulty.isEmpty() && !"all".equalsIgnoreCase(difficulty)) {
            query = query.whereEqualTo("difficulty", difficulty);
        }
        query = query.orderBy("timeSec", Query.Direction.ASCENDING).limit(limit);
        QuerySnapshot snapshot = query.get().get();
        return mapScores(snapshot);
    }

    public List<Score> getAllScores(String difficulty, String uid, Boolean flagged) throws ExecutionException, InterruptedException {
        Query query = db.collection("scores");
        if (difficulty != null && !difficulty.isEmpty() && !"all".equalsIgnoreCase(difficulty)) {
            query = query.whereEqualTo("difficulty", difficulty);
        }
        if (uid != null && !uid.isEmpty()) {
            query = query.whereEqualTo("uid", uid);
        }
        if (flagged != null) {
            query = query.whereEqualTo("flagged", flagged);
        }
        query = query.orderBy("playedAt", Query.Direction.DESCENDING).limit(100);
        return mapScores(query.get().get());
    }

    public List<Score> getScoresByUid(String uid) throws ExecutionException, InterruptedException {
        Query query = db.collection("scores")
                .whereEqualTo("uid", uid)
                .orderBy("playedAt", Query.Direction.DESCENDING);
        QuerySnapshot snapshot = query.get().get();
        return mapScores(snapshot);
    }

    public List<Score> getRecentScores(int limit) throws ExecutionException, InterruptedException {
        Query query = db.collection("scores")
                .orderBy("playedAt", Query.Direction.DESCENDING)
                .limit(limit);
        QuerySnapshot snapshot = query.get().get();
        return mapScores(snapshot);
    }

    public int countWinsByUid(String uid) throws ExecutionException, InterruptedException {
        Query query = db.collection("scores")
                .whereEqualTo("uid", uid)
                .whereEqualTo("result", "win");
        QuerySnapshot snapshot = query.get().get();
        return snapshot.size();
    }

    public void updateFlagged(String scoreId, boolean isFlagged) throws ExecutionException, InterruptedException {
        db.collection("scores").document(scoreId).update("flagged", isFlagged).get();
    }

    public void flagScore(String scoreId, boolean isFlagged) throws ExecutionException, InterruptedException {
        updateFlagged(scoreId, isFlagged);
    }

    public void deleteScore(String scoreId) throws ExecutionException, InterruptedException {
        db.collection("scores").document(scoreId).delete().get();
    }

    private List<Score> mapScores(QuerySnapshot snapshot) {
        List<Score> scores = new ArrayList<>();
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            Score score = doc.toObject(Score.class);
            if (score != null) {
                score.setScoreId(doc.getId());
                scores.add(score);
            }
        }
        return scores;
    }
}

/* Added query filters for game level difficulty scores */

/* Index Firestore optimization scores sorting query */
