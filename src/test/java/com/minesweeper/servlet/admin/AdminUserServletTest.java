package com.minesweeper.servlet.admin;

import com.google.cloud.firestore.Firestore;
import com.minesweeper.dao.UserDAO;
import com.minesweeper.model.User;
import com.minesweeper.util.FirebaseUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminUserServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private HttpSession session;

    @Mock
    private UserDAO userDAO;

    @Mock
    private Firestore firestore;

    private AdminUserServlet adminUserServlet;

    @BeforeEach
    public void setUp() throws Exception {
        try (MockedStatic<FirebaseUtil> firebaseUtilMock = Mockito.mockStatic(FirebaseUtil.class)) {
            firebaseUtilMock.when(FirebaseUtil::getFirestore).thenReturn(firestore);
            adminUserServlet = new AdminUserServlet();
        }

        Field userDAOField = AdminUserServlet.class.getDeclaredField("userDAO");
        userDAOField.setAccessible(true);
        userDAOField.set(adminUserServlet, userDAO);
    }

    @Test
    public void doGet_shouldForwardUsersPage() throws Exception {
        List<User> users = List.of(new User());
        when(userDAO.getAllUsers()).thenReturn(users);
        when(request.getRequestDispatcher("/WEB-INF/views/admin/users.jsp")).thenReturn(dispatcher);

        adminUserServlet.doGet(request, response);

        verify(userDAO, times(1)).getAllUsers();
        verify(request, times(1)).setAttribute("users", users);
        verify(request, times(1)).getRequestDispatcher("/WEB-INF/views/admin/users.jsp");
        verify(dispatcher, times(1)).forward(request, response);
    }

    @Test
    public void doGet_whenDaoFails_shouldSend500() throws Exception {
        when(userDAO.getAllUsers()).thenThrow(new ExecutionException(new RuntimeException("Firestore error")));

        adminUserServlet.doGet(request, response);

        verify(response, times(1)).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(request, never()).getRequestDispatcher("/WEB-INF/views/admin/users.jsp");
    }

    @Test
    public void doPost_block_shouldSetBlockedTrue() throws Exception {
        when(request.getParameter("action")).thenReturn("block");
        when(request.getParameter("uid")).thenReturn("user_001");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("uid")).thenReturn("admin_001");
        when(request.getContextPath()).thenReturn("/minesweeper");

        adminUserServlet.doPost(request, response);

        verify(userDAO, times(1)).updateUserField("user_001", "isBlocked", true);
        verify(response, times(1)).sendRedirect("/minesweeper/admin/users");
    }

    @Test
    public void doPost_unblock_shouldSetBlockedFalse() throws Exception {
        when(request.getParameter("action")).thenReturn("unblock");
        when(request.getParameter("uid")).thenReturn("user_001");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("uid")).thenReturn("admin_001");
        when(request.getContextPath()).thenReturn("/minesweeper");

        adminUserServlet.doPost(request, response);

        verify(userDAO, times(1)).updateUserField("user_001", "isBlocked", false);
        verify(response, times(1)).sendRedirect("/minesweeper/admin/users");
    }

    @Test
    public void doPost_setRole_shouldUpdateRole() throws Exception {
        when(request.getParameter("action")).thenReturn("setRole");
        when(request.getParameter("uid")).thenReturn("user_001");
        when(request.getParameter("role")).thenReturn("ADMIN");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("uid")).thenReturn("admin_001");
        when(request.getContextPath()).thenReturn("/minesweeper");

        adminUserServlet.doPost(request, response);

        verify(userDAO, times(1)).updateUserField("user_001", "role", "admin");
        verify(response, times(1)).sendRedirect("/minesweeper/admin/users");
    }

    @Test
    public void doPost_selfBlock_shouldRedirectError() throws Exception {
        when(request.getParameter("action")).thenReturn("block");
        when(request.getParameter("uid")).thenReturn("admin_001");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("uid")).thenReturn("admin_001");
        when(request.getContextPath()).thenReturn("/minesweeper");

        adminUserServlet.doPost(request, response);

        verify(userDAO, never()).updateUserField(anyString(), anyString(), any());
        verify(response, times(1)).sendRedirect("/minesweeper/admin/users?error=self-block");
    }
}
