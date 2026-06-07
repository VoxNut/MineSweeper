package com.minesweeper.servlet.game;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GameServlet extends HttpServlet {
    @Override
    // UC-8 Chơi game
    // 8.1.1 → 8.1.7: Người chơi truy cập trang game — hiển thị giao diện chơi (game.jsp)
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/game.jsp").forward(request, response);
    }
}
