package com.minesweeper.servlet.score;

import com.minesweeper.model.Score;
import com.minesweeper.service.LeaderboardService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * UC-9: Xem bảng xếp hạng
 * <p>
 * Servlet xử lý yêu cầu HTTP GET tới URL /leaderboard.
 * Đây là trang công khai (public path) — AuthFilter cho phép cả Guest,
 * Player lẫn Admin truy cập mà không cần phiên đăng nhập (UC-9, BR5).
 * <p>
 * Luồng cơ bản (UC-9, Basic Flow 9.1):
 * <ol>
 * <li>9.1.3 — Đọc tham số "difficulty" từ query string.</li>
 * <li>9.1.4 — Gọi LeaderboardService.getTopScores() để lấy dữ liệu.</li>
 * <li>9.1.8 — Gán kết quả vào request attributes.</li>
 * <li>9.1.9 — Forward sang leaderboard.jsp để render giao diện.</li>
 * </ol>
 * Exception Flow (UC-9, 9.4): Nếu Firestore lỗi, trả về HTTP 500.
 */
public class LeaderboardServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(LeaderboardServlet.class.getName());
    private final LeaderboardService leaderboardService = new LeaderboardService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // UC-9 (9.1.3): Đọc tham số difficulty từ query string.
        // Nếu không có tham số, giá trị sẽ là null và LeaderboardService sẽ
        // hiểu là "all" — hiển thị tất cả mức độ khó (UC-9, BR3).
        // UC-9 (9.2.2): Khi người dùng chọn filter và submit form, request GET
        // cũng đi qua đây với difficulty đã được thiết lập.
        String difficulty = request.getParameter("difficulty");
        try {
            // UC-9 (9.1.4 → 9.1.7): Gọi service để lấy top 20 bản ghi thỏa
            // điều kiện result="win", flagged=false, sắp xếp theo timeSec ASC.
            List<Score> scores = leaderboardService.getTopScores(difficulty);

            // UC-9 (9.1.8): Gán danh sách điểm số và bộ lọc difficulty vào
            // request attributes để leaderboard.jsp có thể đọc và render.
            request.setAttribute("scores", scores);
            request.setAttribute("difficulty", difficulty);

            // UC-9 (9.1.9): Forward sang View để render trang bảng xếp hạng.
            request.getRequestDispatcher("/WEB-INF/views/leaderboard.jsp").forward(request, response);
        } catch (Exception ex) {
            // UC-9 (9.4.2 → 9.4.3): Nếu Firestore lỗi (ExecutionException /
            // InterruptedException), ghi log SEVERE và trả về HTTP 500.
            LOGGER.log(Level.SEVERE, "Failed to load leaderboard", ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
