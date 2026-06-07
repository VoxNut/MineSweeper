package com.minesweeper.servlet.auth;

import com.minesweeper.model.User;
import com.minesweeper.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
 * Unit Test – UC-03: Đăng ký tài khoản
 * Bảng 6 – kiểm thử luồng đăng ký qua LoginServlet.doPost()
 *
 * Luồng UC-03: Frontend tạo tài khoản Firebase Auth → gọi POST /auth/login
 * với idToken → Backend (LoginServlet) upsert User vào Firestore → tạo session.
 */
@ExtendWith(MockitoExtension.class)
public class RegisterServletTest {

    @Mock private HttpServletRequest  request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession         session;
    @Mock private AuthService         authService;

    @InjectMocks
    private LoginServlet loginServlet;

    @BeforeEach
    public void setUp() {
        // Inject mock AuthService vào servlet (field package-private)
        loginServlet.authService = authService;
    }

    // -----------------------------------------------------------------------
    // TC-2 | User model mặc định cho tài khoản mới đăng ký (role=player, elo=1000)
    // -----------------------------------------------------------------------
    @Test
    public void testNewUserModel_defaultRegistrationValues() {
        // Mô phỏng bước 3.1.10: Backend tạo User mới với role="player", elo=1000
        User newUser = new User();
        newUser.setUid("new_uid_001");
        newUser.setEmail("newuser@example.com");
        newUser.setDisplayName("New User");
        newUser.setRole("player");
        newUser.setBlocked(false);
        newUser.setEloRating(1000);

        assertEquals("new_uid_001",         newUser.getUid());
        assertEquals("newuser@example.com", newUser.getEmail());
        assertEquals("New User",            newUser.getDisplayName());
        assertEquals("player",              newUser.getRole(),
                "BR3: Người dùng mới phải có role=player");
        assertFalse(newUser.isBlocked(),    "Tài khoản mới không bị khóa");
        assertEquals(1000,                  newUser.getEloRating(),
                "BR3: Điểm ELO mặc định phải là 1000");
    }

    // -----------------------------------------------------------------------
    // TC-3 | AuthResult lưu đúng tất cả trường sau khi đăng ký thành công
    // -----------------------------------------------------------------------
    @Test
    public void testAuthResult_storesAllFieldsCorrectly() {
        AuthService.AuthResult result = new AuthService.AuthResult(
                "uid_new", "Registered User", "player",
                "https://photo.url/avatar.jpg", false);

        assertEquals("uid_new",          result.getUid());
        assertEquals("Registered User",  result.getDisplayName());
        assertEquals("player",           result.getRole());
        assertEquals("https://photo.url/avatar.jpg", result.getPhotoURL());
        assertFalse(result.isBlocked(), "Tài khoản vừa đăng ký không bị blocked");
    }

    // -----------------------------------------------------------------------
    // TC-1 | POST đăng ký thành công → HTTP 200, role=player, session được tạo
    // -----------------------------------------------------------------------
    @Test
    public void testDoPost_newUserRegister_returns200_playerRole() throws Exception {
        // Bước 3.1.12: Frontend gửi idToken sau khi Firebase tạo tài khoản xong
        AuthService.AuthResult newUserResult =
                new AuthService.AuthResult("uid_new_001", "New Player", "player", "", false);
        when(authService.verifyAndUpsert(anyString())).thenReturn(newUserResult);

        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));
        when(request.getReader()).thenReturn(
                new BufferedReader(new StringReader("{\"idToken\":\"new.user.firebase.token\"}")));
        when(request.getSession(true)).thenReturn(session);

        loginServlet.doPost(request, response);

        String body = sw.toString();
        assertTrue(body.contains("true"),   "Response phải chứa success=true");
        assertTrue(body.contains("player"), "Response phải chứa role=player");

        // Bước 3.1.12: Session được tạo và gán đúng thuộc tính
        verify(session).setAttribute(eq("uid"),         eq("uid_new_001"));
        verify(session).setAttribute(eq("displayName"), eq("New Player"));
        verify(session).setAttribute(eq("role"),        eq("player"));
    }

    // -----------------------------------------------------------------------
    // TC-7 | Session sau đăng ký chứa đúng uid, displayName, role, photoURL
    // -----------------------------------------------------------------------
    @Test
    public void testDoPost_register_sessionHasAllCorrectAttributes() throws Exception {
        AuthService.AuthResult result = new AuthService.AuthResult(
                "uid_abc", "Khai Nguyen", "player", "https://avatar.png", false);
        when(authService.verifyAndUpsert(anyString())).thenReturn(result);

        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));
        when(request.getReader()).thenReturn(
                new BufferedReader(new StringReader("{\"idToken\":\"khai.abc.token\"}")));
        when(request.getSession(true)).thenReturn(session);

        loginServlet.doPost(request, response);

        verify(session).setAttribute("uid",         "uid_abc");
        verify(session).setAttribute("displayName", "Khai Nguyen");
        verify(session).setAttribute("role",        "player");
        verify(session).setAttribute("photoURL",    "https://avatar.png");
    }

    // -----------------------------------------------------------------------
    // TC-4 | Tài khoản bị blocked sau khi tạo → HTTP 403, không tạo session
    // -----------------------------------------------------------------------
    @Test
    public void testDoPost_blockedNewUser_returns403_noSession() throws Exception {
        AuthService.AuthResult blockedResult =
                new AuthService.AuthResult("uid_blocked_new", "Blocked", "player", "", true);
        when(authService.verifyAndUpsert(anyString())).thenReturn(blockedResult);

        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));
        when(request.getReader()).thenReturn(
                new BufferedReader(new StringReader("{\"idToken\":\"blocked.new.token\"}")));

        loginServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        assertTrue(sw.toString().contains("blocked"));
        verify(request, never()).getSession(true);
    }

    // -----------------------------------------------------------------------
    // TC-5 | idToken không hợp lệ khi đồng bộ sau đăng ký → HTTP 500
    // -----------------------------------------------------------------------
    @Test
    public void testDoPost_invalidToken_afterRegister_returns500() throws Exception {
        // Bước 3.4.1: Firebase Admin báo lỗi idToken không hợp lệ
        when(authService.verifyAndUpsert(anyString()))
                .thenThrow(new RuntimeException("Token expired or invalid"));

        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));
        when(request.getReader()).thenReturn(
                new BufferedReader(new StringReader("{\"idToken\":\"expired.token\"}")));

        loginServlet.doPost(request, response);

        // Bước 3.4.2: Backend trả về HTTP 500
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        assertTrue(sw.toString().contains("Login failed"));
        verify(request, never()).getSession(true);
    }

    // -----------------------------------------------------------------------
    // TC-6 | Body POST không chứa idToken → HTTP 400 Bad Request
    // -----------------------------------------------------------------------
    @Test
    public void testDoPost_missingIdToken_returns400() throws Exception {
        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));
        when(request.getReader()).thenReturn(
                new BufferedReader(new StringReader("{\"wrongField\":\"value\"}")));

        loginServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(sw.toString().contains("Missing idToken"));
    }
}
