package com.minesweeper.servlet.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminLoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(request, response);
    }
}
// Commit: Sao lưu toàn bộ file mã nguồn chính (thư mục source code) dự phòng | Author: Võ Minh Nhựt | Date: 2026-06-05 23:30:00
