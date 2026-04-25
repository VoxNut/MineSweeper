package com.minesweeper.servlet.game;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GameServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/game.jsp").forward(request, response);
    }
}

// Commit: Tạo trang giao diện chơi game (game.jsp) và GameServlet | Author: Nguyễn Đức Khải | Date: 2026-04-25 21:10:22
