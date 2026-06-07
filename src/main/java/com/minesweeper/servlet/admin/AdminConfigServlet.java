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

public class AdminConfigServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AdminConfigServlet.class.getName());
    private final GameConfigDAO gameConfigDAO = new GameConfigDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
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
            GameConfig config = buildConfig(request);
            List<String> errors = validate(config);
            if (!errors.isEmpty()) {
                request.setAttribute("errors", errors);
                request.setAttribute("config", config);
                request.getRequestDispatcher("/WEB-INF/views/admin/config.jsp").forward(request, response);
                return;
            }

            HttpSession session = request.getSession(false);
            String adminUid = session != null ? (String) session.getAttribute("uid") : "";
            config.setUpdatedAt(Timestamp.now());
            config.setUpdatedBy(adminUid);
            gameConfigDAO.updateConfig(config, adminUid);
            request.getServletContext().removeAttribute("gameConfigCache");
            request.getServletContext().removeAttribute("gameConfigCacheTime");
            response.sendRedirect(request.getContextPath() + "/admin/config");
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.log(Level.SEVERE, "Failed to update config", ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private GameConfig buildConfig(HttpServletRequest request) {
        GameConfig config = new GameConfig();
        config.setEasy(parseDifficulty(request, "easy"));
        config.setMedium(parseDifficulty(request, "medium"));
        config.setHard(parseDifficulty(request, "hard"));
        return config;
    }

    private GameConfig.DifficultyConfig parseDifficulty(HttpServletRequest request, String prefix) {
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
        List<String> errors = new ArrayList<>();
        validateDifficulty(errors, "Easy", config.getEasy());
        validateDifficulty(errors, "Medium", config.getMedium());
        validateDifficulty(errors, "Hard", config.getHard());
        return errors;
    }

    private void validateDifficulty(List<String> errors, String label, GameConfig.DifficultyConfig difficulty) {
        if (difficulty.getRows() < 5 || difficulty.getRows() > 30) {
            errors.add(label + ": rows must be between 5 and 30");
        }
        if (difficulty.getCols() < 5 || difficulty.getCols() > 50) {
            errors.add(label + ": cols must be between 5 and 50");
        }
        if (difficulty.getMines() >= difficulty.getRows() * difficulty.getCols() - 9) {
            errors.add(label + ": mines must be less than total cells minus 9 safe cells");
        }
    }
}