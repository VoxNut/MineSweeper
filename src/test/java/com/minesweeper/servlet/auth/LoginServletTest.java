package com.minesweeper.servlet.auth;

import com.minesweeper.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit Test – UC-2: Đăng nhập bằng Email và Password
 * Bảng 5 – kiểm thử các kịch bản của LoginServlet.doPost()
 */
@ExtendWith(MockitoExtension.class)
public class LoginServletTest {

    @Mock private HttpServletRequest  request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession         session;
    @Mock private RequestDispatcher   dispatcher;
    @Mock private AuthService         authService;

    @InjectMocks
    private LoginServlet loginServlet;

    @BeforeEach
    public void setUp() {
        // Inject mock AuthService vào servlet (field package-private)
        loginServlet.authService = authService;
    }

    // -----------------------------------------------------------------------
    // TC-6 | GET /login → forward tới login.jsp
    // -----------------------------------------------------------------------
    @Test
    public void testDoGet_forwardsToLoginPage() throws Exception {
        when(request.getRequestDispatcher("/WEB-INF/views/login.jsp")).thenReturn(dispatcher);

        loginServlet.doGet(request, response);

        verify(request,     times(1)).getRequestDispatcher("/WEB-INF/views/login.jsp");
        verify(dispatcher,  times(1)).forward(request, response);
    }

    // -----------------------------------------------------------------------
    // TC-3 | POST thiếu idToken trong body → HTTP 400 Bad Request
    // -----------------------------------------------------------------------
    @Test
    public void testDoPost_missingIdToken_returns400() throws Exception {
        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));
        when(request.getReader()).thenReturn(
                new BufferedReader(new StringReader("{}")));

        loginServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(sw.toString().contains("Missing idToken"));
    }

    // -----------------------------------------------------------------------
    // TC-1 | POST idToken hợp lệ, role=player → HTTP 200, session được tạo
    // -----------------------------------------------------------------------
    @Test
    public void testDoPost_validToken_playerRole_returns200() throws Exception {
        AuthService.AuthResult fakeResult =
                new AuthService.AuthResult("uid_001", "Test User", "player", "", false);
        when(authService.verifyAndUpsert(anyString())).thenReturn(fakeResult);

        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));
        when(request.getReader()).thenReturn(
                new BufferedReader(new StringReader("{\"idToken\":\"valid.player.token\"}")));
        when(request.getSession(true)).thenReturn(session);

        loginServlet.doPost(request, response);

        String body = sw.toString();
        assertTrue(body.contains("true"),   "Response phải chứa success=true");
        assertTrue(body.contains("player"), "Response phải chứa role=player");
        verify(session).setAttribute(eq("uid"),         eq("uid_001"));
        verify(session).setAttribute(eq("displayName"), eq("Test User"));
        verify(session).setAttribute(eq("role"),        eq("player"));
    }

    // -----------------------------------------------------------------------
    // TC-2 | POST idToken hợp lệ, role=admin → HTTP 200, session role=admin
    // -----------------------------------------------------------------------
    @Test
    public void testDoPost_validToken_adminRole_returns200() throws Exception {
        AuthService.AuthResult adminResult =
                new AuthService.AuthResult("uid_admin", "Admin User", "admin", "", false);
        when(authService.verifyAndUpsert(anyString())).thenReturn(adminResult);

        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));
        when(request.getReader()).thenReturn(
                new BufferedReader(new StringReader("{\"idToken\":\"valid.admin.token\"}")));
        when(request.getSession(true)).thenReturn(session);

        loginServlet.doPost(request, response);

        String body = sw.toString();
        assertTrue(body.contains("admin"), "Response phải chứa role=admin");
        verify(session).setAttribute(eq("role"), eq("admin"));
    }

    // -----------------------------------------------------------------------
    // TC-4 | POST tài khoản bị khóa (blocked=true) → HTTP 403, không tạo session
    // -----------------------------------------------------------------------
    @Test
    public void testDoPost_blockedUser_returns403() throws Exception {
        AuthService.AuthResult blockedResult =
                new AuthService.AuthResult("uid_blocked", "Blocked", "player", "", true);
        when(authService.verifyAndUpsert(anyString())).thenReturn(blockedResult);

        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));
        when(request.getReader()).thenReturn(
                new BufferedReader(new StringReader("{\"idToken\":\"blocked.token\"}")));

        loginServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        assertTrue(sw.toString().contains("blocked"));
        verify(request, never()).getSession(true);
    }

    // -----------------------------------------------------------------------
    // TC-5 | POST idToken không hợp lệ / AuthService ném Exception → HTTP 500
    // -----------------------------------------------------------------------
    @Test
    public void testDoPost_invalidToken_returns500() throws Exception {
        when(authService.verifyAndUpsert(anyString()))
                .thenThrow(new RuntimeException("Token verification failed"));

        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));
        when(request.getReader()).thenReturn(
                new BufferedReader(new StringReader("{\"idToken\":\"invalid.token\"}")));

        loginServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        assertTrue(sw.toString().contains("Login failed"));
        verify(request, never()).getSession(true);
    }
}
