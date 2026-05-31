package com.minesweeper.service;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.minesweeper.dao.ScoreDAO;
import com.minesweeper.model.Score;

/**
 * UC-9: Xem bảng xếp hạng — Lớp Service
 * <p>
 * Đóng vai trò trung gian giữa LeaderboardServlet và ScoreDAO.
 * Nhận yêu cầu từ Servlet và ủy thác truy vấn dữ liệu cho lớp DAO.
 */
public class LeaderboardService {
    private final ScoreDAO scoreDAO = new ScoreDAO();

    /**
     * UC-9 (9.1.4 → 9.1.5): Lấy top 20 bản ghi tốt nhất cho bảng xếp hạng.
     * Giới hạn mặc định là 20 bản ghi (UC-9, BR4).
     *
     * @param difficulty mức độ khó cần lọc ("easy", "medium", "hard", "custom",
     *                   "all", hoặc null).
     * @return danh sách Score được sắp xếp theo timeSec tăng dần.
     */
    public List<Score> getTopScores(String difficulty) throws ExecutionException, InterruptedException {
        return scoreDAO.getTopScores(difficulty, 20);
    }

    /**
     * UC-9 (Alternative): Overload cho phép chỉ định giới hạn số lượng bản ghi khác
     * (ví dụ: hiển thị trên trang admin dashboard với giới hạn nhỏ hơn).
     *
     * @param difficulty mức độ khó cần lọc.
     * @param limit      số bản ghi tối đa cần trả về.
     * @return danh sách Score được sắp xếp theo timeSec tăng dần.
     */
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
