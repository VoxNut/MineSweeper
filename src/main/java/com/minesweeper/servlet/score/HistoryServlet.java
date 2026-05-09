package com.minesweeper.servlet.score;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.minesweeper.model.Score;
import com.minesweeper.service.LeaderboardService;

public class HistoryServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(HistoryServlet.class.getName());
    private final LeaderboardService leaderboardService = new LeaderboardService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String uid = session != null ? (String) session.getAttribute("uid") : null;
        if (uid == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        try {
            List<Score> scores = leaderboardService.getHistory(uid);
            int totalGames = scores != null ? scores.size() : 0;
            int winCount = 0;
            int lossCount = 0;
            int totalScore = 0;
            if (scores != null) {
                for (Score score : scores) {
                    if ("win".equalsIgnoreCase(score.getResult())) {
                        winCount += 1;
                        totalScore += 1;
                    } else if ("lose".equalsIgnoreCase(score.getResult())) {
                        lossCount += 1;
                        totalScore -= 1;
                    }
                }
            }
            double winRate = totalGames > 0 ? (winCount * 100.0) / totalGames : 0.0;
            String winRateText = String.format("%.1f%%", winRate);

            request.setAttribute("scores", scores);
            request.setAttribute("totalGames", totalGames);
            request.setAttribute("winCount", winCount);
            request.setAttribute("lossCount", lossCount);
            request.setAttribute("winRateText", winRateText);
            request.setAttribute("totalScore", totalScore);
            request.getRequestDispatcher("/WEB-INF/views/history.jsp").forward(request, response);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to load history", ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
