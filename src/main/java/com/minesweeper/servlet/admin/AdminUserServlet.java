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
            List<User> users = userDAO.getAllUsers();
            request.setAttribute("users", users);
            request.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(request, response);
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.log(Level.SEVERE, "Failed to load users", ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        String uid = request.getParameter("uid");
        HttpSession session = request.getSession(false);
        String currentUid = session != null ? (String) session.getAttribute("uid") : null;

        if (uid == null || uid.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }
        if (currentUid != null && currentUid.equals(uid) && "block".equalsIgnoreCase(action)) {
            response.sendRedirect(request.getContextPath() + "/admin/users?error=self-block");
            return;
        }
        try {
            if ("block".equalsIgnoreCase(action)) {
                userDAO.updateUserField(uid, "isBlocked", true);
            } else if ("unblock".equalsIgnoreCase(action)) {
                userDAO.updateUserField(uid, "isBlocked", false);
            } else if ("setRole".equalsIgnoreCase(action)) {
                String role = request.getParameter("role");
                if ("admin".equalsIgnoreCase(role) || "player".equalsIgnoreCase(role)) {
                    userDAO.updateUserField(uid, "role", role.toLowerCase());
                }
            }
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.log(Level.SEVERE, "Failed to update user", ex);
        }
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
}
