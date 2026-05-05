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

public class LeaderboardServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(LeaderboardServlet.class.getName());
    private final LeaderboardService leaderboardService = new LeaderboardService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String difficulty = request.getParameter("difficulty");
        try {
            List<Score> scores = leaderboardService.getTopScores(difficulty);
            request.setAttribute("scores", scores);
            request.setAttribute("difficulty", difficulty);
            request.getRequestDispatcher("/WEB-INF/views/leaderboard.jsp").forward(request, response);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to load leaderboard", ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
