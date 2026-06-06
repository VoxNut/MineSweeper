package com.minesweeper.servlet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;

/**
 * Unit test cho UC-7: Xem hướng dẫn chơi.
 *
 * Mục tiêu: xác nhận endpoint GET /howtoplay sẽ render đúng trang hướng dẫn chơi.
 * Mapping trong tài liệu:
 * - UC-7 Basic Flow:
 *   - (7.1.1) Người dùng gửi yêu cầu GET /howtoplay.
 *   - (7.1.2) HowToPlayServlet forward tới howtoplay.jsp để render.
 */
public class HowToPlayServletTest {

    @Test
    @DisplayName("TC-UC7-01/TC-UC7-02 - UC-7 / 7.1.1-7.1.2: GET /howtoplay -> forward tới /WEB-INF/views/howtoplay.jsp")
    void doGet_shouldForwardToHowToPlayJsp() throws Exception {
        /**
         * Test Data (Input)
         * - RequestDispatcher path: /WEB-INF/views/howtoplay.jsp
         *
         * Process
         * - [UC-7][7.1.1] Gọi doGet(request, response) để mô phỏng GET /howtoplay.
         * - [UC-7][7.1.2] Servlet phải forward tới view howtoplay.jsp.
         *
         * Expected Output
         * - request.getRequestDispatcher("/WEB-INF/views/howtoplay.jsp") được gọi.
         * - dispatcher.forward(request, response) được gọi.
         * - Không redirect và không sendError.
         */
        // Setup: tạo servlet + mock request/response/dispatcher.
        HowToPlayServlet servlet = new HowToPlayServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getRequestDispatcher("/WEB-INF/views/howtoplay.jsp")).thenReturn(dispatcher);

        // [UC-7][7.1.1] Người dùng gửi yêu cầu GET /howtoplay.
        sysout("START [UC-7][7.1.1] GET /howtoplay");
        servlet.doGet(request, response);
        sysout("OK    [UC-7][7.1.1] Servlet xử lý doGet");

        // [UC-7][7.1.2] Hệ thống forward tới /WEB-INF/views/howtoplay.jsp để render nội dung hướng dẫn.
        sysout("START [UC-7][7.1.2] Forward tới howtoplay.jsp");
        verify(request, times(1)).getRequestDispatcher("/WEB-INF/views/howtoplay.jsp");
        verify(dispatcher, times(1)).forward(request, response);
        verifyNoMoreInteractions(dispatcher);
        sysout("OK    [UC-7][7.1.2] Forward đúng view");

        // [UC-7] Không có redirect / error trong luồng xem hướng dẫn.
        sysout("START [UC-7] Kiểm tra không redirect / sendError");
        verify(response, never()).sendRedirect(anyString());
        verify(response, never()).sendError(anyInt());
        sysout("OK    [UC-7] Không redirect / sendError");

        sysout("PASS [TC-UC7-01/TC-UC7-02] [UC-7][7.1.1-7.1.2] GET /howtoplay -> forward /WEB-INF/views/howtoplay.jsp");
    }

    private static void sysout(String message) {
        System.out.println(message);
    }
}

