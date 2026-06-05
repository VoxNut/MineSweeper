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
            AuthService.AuthResult result = authService.verifyAndUpsert(idToken);
            if (result.isBlocked()) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                writeJson(response, false, "User is blocked");
                return;
            }

            HttpSession session = request.getSession(true);
            session.setAttribute("uid", result.getUid());
            session.setAttribute("displayName", result.getDisplayName());
            session.setAttribute("role", result.getRole());
            session.setAttribute("photoURL", result.getPhotoURL());

            Map<String, Object> payload = new HashMap<>();
            payload.put("success", true);
            payload.put("role", result.getRole());
            gson.toJson(payload, response.getWriter());
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Login failed", ex);
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
