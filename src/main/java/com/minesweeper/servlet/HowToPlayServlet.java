package com.minesweeper.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * UC-7: Xem hướng dẫn chơi.
 *
 * - [UC-7][7.1.1] Người dùng gửi yêu cầu GET /howtoplay.
 * - [UC-7][7.1.2] Hệ thống render trang hướng dẫn bằng cách forward tới howtoplay.jsp.
 *
 * Output:
 * - Forward: /WEB-INF/views/howtoplay.jsp
 */
public class HowToPlayServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // [UC-7][7.1.1] Nhận request GET /howtoplay.
        // [UC-7][7.1.2] Forward tới view howtoplay.jsp để hiển thị hướng dẫn chơi.
        request.getRequestDispatcher("/WEB-INF/views/howtoplay.jsp").forward(request, response);
    }
}
