package com.minesweeper.servlet.admin;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.minesweeper.dao.ScoreDAO;
import com.minesweeper.model.Score;

public class AdminScoreServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AdminScoreServlet.class.getName());
    private final ScoreDAO scoreDAO = new ScoreDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // [UC-14] 14.1.5 / 14.1.6 AdminScoreServlet xử lý GET /admin/scores và đọc các bộ lọc nếu có.
        String difficulty = request.getParameter("difficulty");
        String uid = request.getParameter("uid");
        String flaggedParam = request.getParameter("flagged");
        Boolean flagged = null;
        // [UC-14] 14.1.6 Chuyển tham số flagged dạng chuỗi thành Boolean để truyền xuống DAO.
        if ("true".equalsIgnoreCase(flaggedParam)) {
            flagged = Boolean.TRUE;
        } else if ("false".equalsIgnoreCase(flaggedParam)) {
            flagged = Boolean.FALSE;
        }
        try {
            // [UC-14] 14.1.7 Gọi ScoreDAO để lấy danh sách điểm/lịch sử chơi theo bộ lọc.
            List<Score> scores = scoreDAO.getAllScores(difficulty, uid, flagged);
            // [UC-14] 14.1.14 Gắn danh sách scores và trạng thái bộ lọc vào request.
            request.setAttribute("scores", scores);
            request.setAttribute("difficulty", difficulty == null ? "all" : difficulty);
            request.setAttribute("uid", uid == null ? "" : uid);
            request.setAttribute("flagged", flaggedParam == null ? "" : flaggedParam);
            // [UC-14] 14.1.15 Forward sang scores.jsp để render bảng điểm và bộ lọc.
            request.getRequestDispatcher("/WEB-INF/views/admin/scores.jsp").forward(request, response);
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.log(Level.SEVERE, "Failed to load scores", ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // [UC-14] 14.2.4 / 14.2.5 Xử lý POST /admin/scores và đọc action, scoreId từ request.
        String action = request.getParameter("action");
        String scoreId = request.getParameter("scoreId");
        if (scoreId != null && !scoreId.isEmpty()) {
            try {
                if ("flag".equalsIgnoreCase(action)) {
                    // [UC-14] 14.2.6 Đánh dấu bản ghi điểm là nghi vấn/gian lận.
                    scoreDAO.flagScore(scoreId, true);
                } else if ("unflag".equalsIgnoreCase(action)) {
                    // [UC-14] 14.2.6 Bỏ đánh dấu nghi vấn/gian lận cho bản ghi điểm.
                    scoreDAO.flagScore(scoreId, false);
                } else if ("delete".equalsIgnoreCase(action)) {
                    // [UC-14] 14.2.6 Xóa bản ghi điểm/lịch sử chơi được chọn.
                    scoreDAO.deleteScore(scoreId);
                }
            } catch (InterruptedException | ExecutionException ex) {
                LOGGER.log(Level.SEVERE, "Failed to update score", ex);
            }
        }
        // [UC-14] 14.3.1 / 14.2.10 / 14.2.11 Redirect về /admin/scores để tải lại danh sách sau khi xử lý.
        response.sendRedirect(request.getContextPath() + "/admin/scores");
    }
}
