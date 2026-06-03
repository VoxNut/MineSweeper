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
import javax.servlet.http.HttpSession;

import com.minesweeper.dao.UserDAO;
import com.minesweeper.model.User;

public class AdminUserServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AdminUserServlet.class.getName());
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // [UC-13] 13.1.5 / 13.1.6 AdminUserServlet xử lý GET /admin/users và yêu cầu lấy danh sách người dùng.
            List<User> users = userDAO.getAllUsers();
            // [UC-13] 13.1.12 Gắn danh sách users vào request trước khi render users.jsp.
            request.setAttribute("users", users);
            // [UC-13] 13.1.13 Forward sang màn hình quản lý người dùng của admin.
            request.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(request, response);
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.log(Level.SEVERE, "Failed to load users", ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // [UC-13] 13.2.4 / 13.2.5 Xử lý POST /admin/users và đọc action, uid mục tiêu, currentUid của admin hiện tại.
        String action = request.getParameter("action");
        String uid = request.getParameter("uid");
        HttpSession session = request.getSession(false);
        String currentUid = session != null ? (String) session.getAttribute("uid") : null;

        if (uid == null || uid.isEmpty()) {
            // [UC-13] 13.3.1 Nếu uid rỗng hoặc null, bỏ qua cập nhật và quay lại danh sách người dùng.
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }
        if (currentUid != null && currentUid.equals(uid) && "block".equalsIgnoreCase(action)) {
            // [UC-13] 13.4.1 Ngăn admin tự khóa chính tài khoản admin đang đăng nhập.
            response.sendRedirect(request.getContextPath() + "/admin/users?error=self-block");
            return;
        }
        try {
            if ("block".equalsIgnoreCase(action)) {
                // [UC-13] 13.2.6 Khóa người dùng được chọn bằng cách đặt isBlocked=true.
                userDAO.updateUserField(uid, "isBlocked", true);
            } else if ("unblock".equalsIgnoreCase(action)) {
                // [UC-13] 13.2.6 Mở khóa người dùng được chọn bằng cách đặt isBlocked=false.
                userDAO.updateUserField(uid, "isBlocked", false);
            } else if ("setRole".equalsIgnoreCase(action)) {
                String role = request.getParameter("role");
                if ("admin".equalsIgnoreCase(role) || "player".equalsIgnoreCase(role)) {
                    // [UC-13] 13.2.6 Cập nhật role cho người dùng được chọn khi role gửi lên hợp lệ.
                    userDAO.updateUserField(uid, "role", role.toLowerCase());
                }
            }
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.log(Level.SEVERE, "Failed to update user", ex);
        }
        // [UC-13] 13.2.10 / 13.2.11 Redirect về /admin/users để trình duyệt tải lại danh sách đã cập nhật.
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
}
