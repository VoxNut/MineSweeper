package com.minesweeper.servlet.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.StringWriter;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class LogoutServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @InjectMocks
    private LogoutServlet logoutServlet;

    @Test
    public void testDoPostWithActiveSession() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
        
        logoutServlet.doPost(request, response);
        
        verify(session, times(1)).invalidate();
        verify(response, times(1)).setContentType("application/json;charset=UTF-8");
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("success"));
    }

    @Test
    public void testDoPostWithNoSession() throws Exception {
        when(request.getSession(false)).thenReturn(null);
        
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
        
        logoutServlet.doPost(request, response);
        
        verify(session, never()).invalidate();
        verify(response, times(1)).setContentType("application/json;charset=UTF-8");
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("success"));
    }
}

// Commit: Development testing: Bổ sung các unit test cho UC-9 (Bảng xếp hạng), UC-5 (Đăng xuất), UC-6 (Đăng nhập Admin) | Author: Võ Minh Nhựt | Date: 2026-06-05 10:15:00
