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
        String difficulty = request.getParameter("difficulty");
        String uid = request.getParameter("uid");
        String flaggedParam = request.getParameter("flagged");
        Boolean flagged = null;
        if ("true".equalsIgnoreCase(flaggedParam)) {
            flagged = Boolean.TRUE;
        } else if ("false".equalsIgnoreCase(flaggedParam)) {
            flagged = Boolean.FALSE;
        }
        try {
            List<Score> scores = scoreDAO.getAllScores(difficulty, uid, flagged);
            request.setAttribute("scores", scores);
            request.setAttribute("difficulty", difficulty == null ? "all" : difficulty);
            request.setAttribute("uid", uid == null ? "" : uid);
            request.setAttribute("flagged", flaggedParam == null ? "" : flaggedParam);
            request.getRequestDispatcher("/WEB-INF/views/admin/scores.jsp").forward(request, response);
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.log(Level.SEVERE, "Failed to load scores", ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        String scoreId = request.getParameter("scoreId");
        if (scoreId != null && !scoreId.isEmpty()) {
            try {
                if ("flag".equalsIgnoreCase(action)) {
                    scoreDAO.flagScore(scoreId, true);
                } else if ("unflag".equalsIgnoreCase(action)) {
                    scoreDAO.flagScore(scoreId, false);
                } else if ("delete".equalsIgnoreCase(action)) {
                    scoreDAO.deleteScore(scoreId);
                }
            } catch (InterruptedException | ExecutionException ex) {
                LOGGER.log(Level.SEVERE, "Failed to update score", ex);
            }
        }
        response.sendRedirect(request.getContextPath() + "/admin/scores");
    }
}
