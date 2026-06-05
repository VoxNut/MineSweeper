package com.minesweeper.servlet.auth;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.minesweeper.service.AuthService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());
    private final Gson gson = new Gson();
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            JsonObject body = gson.fromJson(request.getReader(), JsonObject.class);
            if (body == null || !body.has("idToken")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writeJson(response, false, "Missing idToken");
                return;
            }
            String idToken = body.get("idToken").getAsString();
            
            // [UC-01] Các bước 1.1.6, 1.1.7, 1.1.8, 1.1.9, 1.1.10 được thực thi bên trong AuthService
            // [UC-02] Các bước 2.1.7, 2.1.8, 2.1.9, 2.1.10, 2.1.11, 2.2.1, 2.2.2 được thực thi bên trong AuthService
            // [UC-03] Các bước 3.1.8, 3.1.9, 3.1.10, 3.1.11 được thực thi bên trong AuthService
            // [UC-06] Các bước 6.1.7, 6.1.8, 6.1.9, 6.1.10, 6.1.11 được thực thi bên trong AuthService
            AuthService.AuthResult result = authService.verifyAndUpsert(idToken);
            
            // [UC-01] 1.5.1 / [UC-02] 2.7.1 Hệ thống phát hiện thuộc tính blocked của người dùng là true.
            if (result.isBlocked()) {
                // [UC-01] 1.5.2 / [UC-02] 2.7.2 Backend từ chối tạo Session và trả về HTTP 403 Forbidden.
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                writeJson(response, false, "User is blocked");
                return;
            }

            // [UC-01] 1.1.11 / [UC-02] 2.1.12 / [UC-03] 3.1.12 / [UC-06] 6.1.11 Backend tạo HttpSession và lưu các thuộc tính (uid, displayName, role, photoURL).
            HttpSession session = request.getSession(true);
            session.setAttribute("uid", result.getUid());
            session.setAttribute("displayName", result.getDisplayName());
            session.setAttribute("role", result.getRole());
            session.setAttribute("photoURL", result.getPhotoURL());

            Map<String, Object> payload = new HashMap<>();
            payload.put("success", true);
            payload.put("role", result.getRole());
            
            // [UC-01] 1.1.12 / [UC-02] 2.1.13 / [UC-03] 3.1.13 / [UC-06] 6.1.12 Backend trả về kết quả đăng nhập thành công (HTTP 200 OK) kèm role.
            gson.toJson(payload, response.getWriter());
        } catch (Exception ex) {
            // [UC-01] 1.4.1 / [UC-02] 2.6.1 / [UC-03] 3.4.1 Firebase Admin SDK ném ra ngoại lệ khi xác minh token.
            LOGGER.log(Level.SEVERE, "Login failed", ex);
            // [UC-01] 1.4.2 / [UC-02] 2.6.2 / [UC-03] 3.4.2 Backend bắt lỗi và trả về mã HTTP 500.
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(response, false, "Login failed");
        }
    }

    private void writeJson(HttpServletResponse response, boolean success, String message) throws IOException {
        Map<String, Object> payload = new HashMap<>();
        payload.put("success", success);
        payload.put("message", message);
        gson.toJson(payload, response.getWriter());
    }
}
