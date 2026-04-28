package com.minesweeper.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HowToPlayServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/howtoplay.jsp").forward(request, response);
    }
}

// Commit: Hiện thực HowToPlayServlet để điều hướng trang hướng dẫn | Author: Vũ Văn Long | Date: 2026-04-28 20:15:33
