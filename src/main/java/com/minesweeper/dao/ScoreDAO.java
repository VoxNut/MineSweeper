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

/**
 * DAO quản lý thao tác đọc/ghi bản ghi điểm số (Score) trên Firestore.
 * Liên quan tới: UC-9 (Xem bảng xếp hạng), UC-8 (Xem lịch sử cá nhân).
 */

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

    /**
     * UC-9 (9.1.6): Truy vấn Firestore lấy danh sách điểm số hàng đầu.
     * <p>
     * Logic áp dụng (theo thứ tự):
     * <ol>
     * <li>UC-9 (9.1.6.1, BR1): Lọc result = "win" — chỉ vàn thắng mới được
     * xét.</li>
     * <li>UC-9 (9.1.6.2, BR2): Lọc flagged = false — loại bỏ bản ghi bị đánh dấu
     * gian lận.</li>
     * <li>UC-9 (9.1.6.3): Nếu difficulty không phải "all", thêm điều kiện lọc
     * difficulty.</li>
     * <li>UC-9 (9.1.6.4, BR3): Sắp xếp theo timeSec tăng dần — thời gian càng ngắn,
     * thứ hạng càng cao.</li>
     * <li>UC-9 (9.1.6.5, BR4): Giới hạn số bản ghi trả về theo tham số limit (mặc
     * định 20).</li>
     * </ol>
     *
     * @param difficulty mức độ khó cần lọc (null / "all" = không lọc theo độ khó).
     * @param limit      số bản ghi tối đa cần trả về.
     * @return danh sách Score được sắp xếp theo timeSec tăng dần.
     */
    public List<Score> getTopScores(String difficulty, int limit) throws ExecutionException, InterruptedException {
        // UC-9 (9.1.6.1, BR1): Chỉ lấy các ván thắng (result = "win").
        // UC-9 (9.1.6.2, BR2): Loại bỏ bản ghi bị đánh dấu gian lận (flagged = false).
        Query query = db.collection("scores")
                .whereEqualTo("result", "win")
                .whereEqualTo("flagged", false);
        // UC-9 (9.1.6.3): Thêm điều kiện lọc theo difficulty nếu không phải "all".
        if (difficulty != null && !difficulty.isEmpty() && !"all".equalsIgnoreCase(difficulty)) {
            query = query.whereEqualTo("difficulty", difficulty);
        }
        // UC-9 (9.1.6.4, BR3): Sắp xếp theo timeSec tăng dần.
        // UC-9 (9.1.6.5, BR4): Giới hạn kết quả (mặc định 20 bản ghi).
        query = query.orderBy("timeSec", Query.Direction.ASCENDING).limit(limit);
        QuerySnapshot snapshot = query.get().get();
        // UC-9 (9.1.7): Ánh xạ kết quả Firestore thành danh sách đối tượng Score.
        return mapScores(snapshot);
    }

    public List<Score> getAllScores(String difficulty, String uid, Boolean flagged)
            throws ExecutionException, InterruptedException {
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
