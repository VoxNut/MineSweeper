package com.minesweeper.servlet.admin;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.minesweeper.util.FirebaseUtil;

public class AdminDashboardServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AdminDashboardServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            QuerySnapshot usersSnapshot = FirebaseUtil.getFirestore().collection("users").get().get();
            QuerySnapshot scoresSnapshot = FirebaseUtil.getFirestore().collection("scores").get().get();
            int totalUsers = usersSnapshot.size();
            int totalGames = scoresSnapshot.size();
            
            int wins = 0;
            int losses = 0;
            int flaggedScores = 0;
            int easy = 0, medium = 0, hard = 0, custom = 0;
            int newUsersToday = 0;

            LocalDate today = LocalDate.now(ZoneId.systemDefault());
            Timestamp startOfToday = Timestamp.ofTimeSecondsAndNanos(today.atStartOfDay(ZoneId.systemDefault()).toEpochSecond(), 0);

            for (DocumentSnapshot doc : usersSnapshot.getDocuments()) {
                Timestamp createdAt = doc.getTimestamp("createdAt");
                if (createdAt != null && !createdAt.toDate().before(startOfToday.toDate())) {
                    newUsersToday++;
                }
            }

            for (DocumentSnapshot doc : scoresSnapshot.getDocuments()) {
                String result = doc.getString("result");
                if ("win".equals(result)) wins++;
                else losses++;

                Boolean isFlagged = doc.getBoolean("flagged");
                if (isFlagged != null && isFlagged) flaggedScores++;

                String difficulty = doc.getString("difficulty");
                switch (difficulty == null ? "custom" : difficulty) {
                    case "easy" -> easy++;
                    case "medium" -> medium++;
                    case "hard" -> hard++;
                    default -> custom++;
                }
            }

            double winRate = totalGames > 0 ? (wins * 100.0 / totalGames) : 0.0;

            request.setAttribute("totalUsers", totalUsers);
            request.setAttribute("totalGames", totalGames);
            request.setAttribute("winRate", String.format("%.1f", winRate) + "%");
            request.setAttribute("flaggedScores", flaggedScores);
            request.setAttribute("easyGames", easy);
            request.setAttribute("mediumGames", medium);
            request.setAttribute("hardGames", hard);
            request.setAttribute("customGames", custom);
            request.setAttribute("newUsersToday", newUsersToday);
            request.setAttribute("wins", wins);
            request.setAttribute("losses", losses);

            request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.log(Level.SEVERE, "Failed to load dashboard", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
