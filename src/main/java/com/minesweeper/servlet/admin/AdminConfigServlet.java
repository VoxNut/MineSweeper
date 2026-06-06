package com.minesweeper.servlet.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.cloud.Timestamp;
import com.minesweeper.dao.GameConfigDAO;
import com.minesweeper.model.GameConfig;

/**
 * UC-15: Cấu hình game (Độ khó).
 *
 * Mục tiêu:
 * - Cho phép Admin cấu hình thông số độ khó (Easy/Medium/Hard) gồm rows/cols/mines.
 *
 * - [UC-15][15.1.1] Admin gửi yêu cầu GET /admin/config.
 * - [UC-15][15.1.2] Server tải cấu hình hiện tại và render trang admin/config.jsp.
 * - [UC-15][15.1.3] Admin nhập rows/cols/mines và submit POST /admin/config.
 * - [UC-15][15.1.4] Backend validate, lưu cấu hình và redirect về /admin/config.
 * - [UC-15][15.2.1] Dữ liệu không hợp lệ: forward lại config.jsp kèm errors.
 *
 * Business Rules:
 * - [UC-15][BR2] rows trong [5..30], cols trong [5..50].
 * - [UC-15][BR3] mines < rows*cols - 9.
 *
 * Output:
 * - Forward: /WEB-INF/views/admin/config.jsp
 * - Redirect: {contextPath}/admin/config
 */
public class AdminConfigServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AdminConfigServlet.class.getName());
    private final GameConfigDAO gameConfigDAO = new GameConfigDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // [UC-15][15.1.1] Nhận request GET /admin/config.
            // [UC-15][15.1.2] Tải cấu hình hiện tại để render giao diện cấu hình.
            gameConfigDAO.ensureDefaultConfig();
            GameConfig config = gameConfigDAO.getDefaultConfig();
            request.setAttribute("config", config);
            HttpSession session = request.getSession(false);
            if (session != null) {
                request.setAttribute("updatedByDisplay", session.getAttribute("displayName"));
            }
            request.getRequestDispatcher("/WEB-INF/views/admin/config.jsp").forward(request, response);
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.log(Level.SEVERE, "Failed to load config", ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // [UC-15][15.1.3] Nhận dữ liệu từ form cấu hình (easy/medium/hard).
            GameConfig config = buildConfig(request);
            // [UC-15][15.1.4] Validate dữ liệu theo BR2/BR3 trước khi lưu.
            List<String> errors = validate(config);
            if (!errors.isEmpty()) {
                // [UC-15][15.2.1] Dữ liệu không hợp lệ: trả errors và forward lại config.jsp.
                request.setAttribute("errors", errors);
                request.setAttribute("config", config);
                request.getRequestDispatcher("/WEB-INF/views/admin/config.jsp").forward(request, response);
                return;
            }

            // [UC-15][15.1.4] Dữ liệu hợp lệ: set metadata updatedAt/updatedBy và lưu cấu hình.
            HttpSession session = request.getSession(false);
            String adminUid = session != null ? (String) session.getAttribute("uid") : "";
            config.setUpdatedAt(Timestamp.now());
            config.setUpdatedBy(adminUid);
            gameConfigDAO.updateConfig(config, adminUid);
            // [UC-15][15.1.4] Clear cache để các luồng khác lấy cấu hình mới.
            request.getServletContext().removeAttribute("gameConfigCache");
            request.getServletContext().removeAttribute("gameConfigCacheTime");
            // [UC-15][15.1.4] Redirect về trang cấu hình sau khi lưu thành công.
            response.sendRedirect(request.getContextPath() + "/admin/config");
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.log(Level.SEVERE, "Failed to update config", ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private GameConfig buildConfig(HttpServletRequest request) {
        // [UC-15][15.1.3] Build GameConfig từ request parameter theo từng prefix (easy/medium/hard).
        GameConfig config = new GameConfig();
        config.setEasy(parseDifficulty(request, "easy"));
        config.setMedium(parseDifficulty(request, "medium"));
        config.setHard(parseDifficulty(request, "hard"));
        return config;
    }

    private GameConfig.DifficultyConfig parseDifficulty(HttpServletRequest request, String prefix) {
        // Input: {prefix}_rows, {prefix}_cols, {prefix}_mines.
        // Fallback chỉ dùng khi param không parse được (NumberFormatException).
        return new GameConfig.DifficultyConfig(
                parseInt(request.getParameter(prefix + "_rows"), 9),
                parseInt(request.getParameter(prefix + "_cols"), 9),
                parseInt(request.getParameter(prefix + "_mines"), 10)
        );
    }

    private int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    private List<String> validate(GameConfig config) {
        // [UC-15][15.1.4] Validate từng độ khó theo BR2/BR3.
        List<String> errors = new ArrayList<>();
        validateDifficulty(errors, "Easy", config.getEasy());
        validateDifficulty(errors, "Medium", config.getMedium());
        validateDifficulty(errors, "Hard", config.getHard());
        return errors;
    }

    private void validateDifficulty(List<String> errors, String label, GameConfig.DifficultyConfig difficulty) {
        // [UC-15][BR2] rows trong [5..30].
        if (difficulty.getRows() < 5 || difficulty.getRows() > 30) {
            errors.add(label + ": rows must be between 5 and 30");
        }
        // [UC-15][BR2] cols trong [5..50].
        if (difficulty.getCols() < 5 || difficulty.getCols() > 50) {
            errors.add(label + ": cols must be between 5 and 50");
        }
        // [UC-15][BR3] mines < rows*cols - 9 (đảm bảo vùng an toàn tối thiểu 9 ô).
        if (difficulty.getMines() >= difficulty.getRows() * difficulty.getCols() - 9) {
            errors.add(label + ": mines must be less than total cells minus 9 safe cells");
        }
    }
}
