package com.minesweeper.servlet.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminLoginServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher dispatcher;

    @InjectMocks
    private AdminLoginServlet adminLoginServlet;

    @Test
    public void testDoGet() throws Exception {
        when(request.getRequestDispatcher("/WEB-INF/views/admin/login.jsp")).thenReturn(dispatcher);
        
        adminLoginServlet.doGet(request, response);
        
        verify(request, times(1)).getRequestDispatcher("/WEB-INF/views/admin/login.jsp");
        verify(dispatcher, times(1)).forward(request, response);
    }
}

// Commit: Development testing: Bổ sung các unit test cho UC-9 (Bảng xếp hạng), UC-5 (Đăng xuất), UC-6 (Đăng nhập Admin) | Author: Võ Minh Nhựt | Date: 2026-06-05 10:15:00
