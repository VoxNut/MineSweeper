package com.minesweeper.servlet.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.minesweeper.dao.GameConfigDAO;
import com.minesweeper.model.GameConfig;

/**
 * Unit test cho UC-15: Cấu hình game (Độ khó).
 *
 * Mục tiêu:
 * - Kiểm tra luồng GET tải cấu hình và forward trang config.jsp.
 * - Kiểm tra validate dữ liệu đầu vào (rows/cols/mines) và xử lý lỗi (forward lại config.jsp kèm errors).
 * - Kiểm tra luồng hợp lệ: gọi DAO.updateConfig và redirect về /admin/config.
 *
 * Mapping trong tài liệu:
 * - UC-15 Basic Flow:
 *   - (15.1.1) Admin gửi yêu cầu GET /admin/config.
 *   - (15.1.2) Server tải cấu hình hiện tại và render trang admin/config.jsp.
 *   - (15.1.3) Admin nhập rows/cols/mines và submit POST /admin/config.
 *   - (15.1.4) Backend validate, lưu cấu hình và redirect về /admin/config.
 * - UC-15 Exception Flow:
 *   - (15.2.1) Dữ liệu không hợp lệ: forward lại config.jsp kèm errors.
 * - UC-15 Business Rules:
 *   - (BR2) rows trong [5..30], cols trong [5..50]
 *   - (BR3) mines < rows*cols - 9
 */
public class AdminConfigServletTest {

    @Test
    @DisplayName("TC-UC15-01 - UC-15 / 15.1.1-15.1.2: GET /admin/config -> load config + forward config.jsp")
    void doGet_shouldLoadConfigAndForward() throws Exception {
        sysout("TC-UC15-01 - UC-15 / 15.1.1-15.1.2: GET /admin/config -> load config + forward config.jsp");
        /**
         * Test Data (Input)
         * - Session displayName: "Admin A"
         * - DAO.getDefaultConfig(): GameConfig.defaultConfig()
         * - Dispatcher path: /WEB-INF/views/admin/config.jsp
         *
         * Process
         * - [UC-15][15.1.1] Gọi doGet(request, response) để mô phỏng GET /admin/config.
         * - [UC-15][15.1.2] Servlet gọi ensureDefaultConfig(), getDefaultConfig(),
         *   setAttribute("config", ...), setAttribute("updatedByDisplay", displayName), rồi forward tới config.jsp.
         *
         * Expected Output
         * - DAO.ensureDefaultConfig() và DAO.getDefaultConfig() được gọi đúng 1 lần.
         * - request.setAttribute("config", GameConfig) được gọi.
         * - request.setAttribute("updatedByDisplay", "Admin A") được gọi.
         * - dispatcher.forward(request, response) được gọi.
         */
        // Setup: tạo servlet + mock DAO/request/response/dispatcher/session.
        AdminConfigServlet servlet = new AdminConfigServlet();

        GameConfigDAO daoMock = mock(GameConfigDAO.class);
        injectGameConfigDao(servlet, daoMock);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getRequestDispatcher("/WEB-INF/views/admin/config.jsp")).thenReturn(dispatcher);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("displayName")).thenReturn("Admin A");

        GameConfig config = GameConfig.defaultConfig();
        doNothing().when(daoMock).ensureDefaultConfig();
        when(daoMock.getDefaultConfig()).thenReturn(config);

        // [UC-15][15.1.1] Admin gửi yêu cầu GET /admin/config.
        sysout("START [UC-15][15.1.1] GET /admin/config");
        servlet.doGet(request, response);
        sysout("OK    [UC-15][15.1.1] Servlet xử lý doGet");

        // [UC-15][15.1.2] Server tải cấu hình và forward tới config.jsp để render.
        sysout("START [UC-15][15.1.2] Load config + forward config.jsp");
        verify(daoMock, times(1)).ensureDefaultConfig();
        verify(daoMock, times(1)).getDefaultConfig();

        ArgumentCaptor<GameConfig> configCaptor = ArgumentCaptor.forClass(GameConfig.class);
        verify(request, times(1)).setAttribute(eq("config"), configCaptor.capture());
        assertNotNull(configCaptor.getValue());

        verify(request, times(1)).setAttribute("updatedByDisplay", "Admin A");
        verify(dispatcher, times(1)).forward(request, response);
        verify(response, never()).sendRedirect(anyString());
        verify(response, never()).sendError(anyInt());
        sysout("OK    [UC-15][15.1.2] Forward thành công, không redirect / sendError");
    }

    @Test
    @DisplayName("TC-UC15-03 - UC-15 / 15.2.1 + BR3: mines không hợp lệ -> forward config.jsp + errors")
    void doPost_invalidMines_shouldForwardWithErrors() throws Exception {
        sysout("TC-UC15-03 - UC-15 / 15.2.1 + BR3: mines không hợp lệ -> forward config.jsp + errors");
        /**
         * Test Data (Input)
         * - Easy: rows=5, cols=5, mines=30
         * - Medium: rows=16, cols=16, mines=40
         * - Hard: rows=16, cols=30, mines=99
         *
         * Business Rule áp dụng
         * - [UC-15][BR3] mines < rows*cols - 9
         *   Với rows=5, cols=5 => rows*cols - 9 = 16, mines=30 => vi phạm BR3.
         *
         * Process
         * - [UC-15][15.1.3] Gọi doPost(request, response) để mô phỏng POST /admin/config với dữ liệu từ form.
         * - [UC-15][15.2.1] Dữ liệu không hợp lệ: servlet setAttribute("errors", ...) và forward lại config.jsp.
         *
         * Expected Output
         * - Có request.setAttribute("errors", List<String>) và list chứa lỗi mines cho Easy.
         * - dispatcher.forward(request, response) được gọi.
         * - Không có response.sendRedirect(...).
         * - Không gọi daoMock.updateConfig(...) (không ghi DB khi dữ liệu sai).
         */
        // Setup: tạo servlet + mock DAO/request/response/dispatcher.
        AdminConfigServlet servlet = new AdminConfigServlet();

        GameConfigDAO daoMock = mock(GameConfigDAO.class);
        injectGameConfigDao(servlet, daoMock);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getRequestDispatcher("/WEB-INF/views/admin/config.jsp")).thenReturn(dispatcher);

        // [UC-15][15.1.3] Admin nhập rows/cols/mines cho Easy/Medium/Hard và submit.
        // [UC-15][BR3] mines < rows*cols - 9. rows=5, cols=5 => rows*cols-9 = 16, mines=30 là vi phạm.
        when(request.getParameter("easy_rows")).thenReturn("5");
        when(request.getParameter("easy_cols")).thenReturn("5");
        when(request.getParameter("easy_mines")).thenReturn("30");

        when(request.getParameter("medium_rows")).thenReturn("16");
        when(request.getParameter("medium_cols")).thenReturn("16");
        when(request.getParameter("medium_mines")).thenReturn("40");

        when(request.getParameter("hard_rows")).thenReturn("16");
        when(request.getParameter("hard_cols")).thenReturn("30");
        when(request.getParameter("hard_mines")).thenReturn("99");

        // [UC-15][15.1.3] Thực thi xử lý POST /admin/config.
        sysout("START [UC-15][15.1.3] POST /admin/config (invalid mines)");
        servlet.doPost(request, response);
        sysout("OK    [UC-15][15.1.3] Servlet xử lý doPost");

        ArgumentCaptor<List<String>> errorsCaptor = stringListCaptor();

        // [UC-15][15.2.1] Dữ liệu không hợp lệ: trả về danh sách errors cho view.
        sysout("START [UC-15][15.2.1] Validate fail -> errors + forward");
        verify(request).setAttribute(eq("errors"), errorsCaptor.capture());

        List<String> errors = errorsCaptor.getValue();
        assertNotNull(errors);
        assertFalse(errors.isEmpty());
        boolean foundEasyMinesError = false;
        for (String error : errors) {
            if (error == null) {
                continue;
            }
            if (error.contains("Easy") && error.toLowerCase().contains("mines")) {
                foundEasyMinesError = true;
                break;
            }
        }
        assertTrue(foundEasyMinesError, "Expected an error message for invalid mines on Easy difficulty.");

        // [UC-15][15.2.1] Forward lại config.jsp, KHÔNG redirect.
        verify(request).setAttribute(eq("config"), any());
        verify(dispatcher).forward(request, response);
        verify(response, never()).sendRedirect(anyString());

        // [UC-15][15.2.1] Dữ liệu sai không được phép ghi DB.
        verifyNoInteractions(daoMock);
        sysout("OK    [UC-15][15.2.1] Forward config.jsp, không ghi DB");
    }

    @Test
    @DisplayName("TC-UC15-02 - UC-15 / 15.2.1 + BR2: rows ngoài [5..30] -> forward config.jsp + errors")
    void doPost_invalidRows_shouldForwardWithErrors() throws Exception {
        sysout("TC-UC15-02 - UC-15 / 15.2.1 + BR2: rows ngoài [5..30] -> forward config.jsp + errors");
        /**
         * Test Data (Input)
         * - Easy: rows=2, cols=9, mines=10  (rows vi phạm BR2)
         * - Medium: rows=16, cols=16, mines=40
         * - Hard: rows=16, cols=30, mines=99
         *
         * Business Rule áp dụng
         * - [UC-15][BR2] rows trong [5..30]
         *
         * Process
         * - [UC-15][15.1.3] POST /admin/config.
         * - [UC-15][15.2.1] Dữ liệu không hợp lệ: forward lại config.jsp kèm errors.
         *
         * Expected Output
         * - errors có chứa thông báo liên quan rows của Easy.
         * - Không gọi dao.updateConfig(...).
         */
        // Setup: tạo servlet + mock DAO/request/response/dispatcher.
        AdminConfigServlet servlet = new AdminConfigServlet();

        GameConfigDAO daoMock = mock(GameConfigDAO.class);
        injectGameConfigDao(servlet, daoMock);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getRequestDispatcher("/WEB-INF/views/admin/config.jsp")).thenReturn(dispatcher);

        // [UC-15][15.1.3] Input từ form (rows sai).
        when(request.getParameter("easy_rows")).thenReturn("2");
        when(request.getParameter("easy_cols")).thenReturn("9");
        when(request.getParameter("easy_mines")).thenReturn("10");

        when(request.getParameter("medium_rows")).thenReturn("16");
        when(request.getParameter("medium_cols")).thenReturn("16");
        when(request.getParameter("medium_mines")).thenReturn("40");

        when(request.getParameter("hard_rows")).thenReturn("16");
        when(request.getParameter("hard_cols")).thenReturn("30");
        when(request.getParameter("hard_mines")).thenReturn("99");

        // [UC-15][15.1.3] Thực thi xử lý POST /admin/config.
        sysout("START [UC-15][15.1.3] POST /admin/config (invalid rows)");
        servlet.doPost(request, response);
        sysout("OK    [UC-15][15.1.3] Servlet xử lý doPost");

        ArgumentCaptor<List<String>> errorsCaptor = stringListCaptor();
        sysout("START [UC-15][15.2.1] Validate fail -> errors + forward");
        verify(request).setAttribute(eq("errors"), errorsCaptor.capture());

        List<String> errors = errorsCaptor.getValue();
        assertNotNull(errors);
        assertFalse(errors.isEmpty());

        boolean foundEasyRowsError = false;
        for (String error : errors) {
            if (error == null) {
                continue;
            }
            String lower = error.toLowerCase();
            if (error.contains("Easy") && lower.contains("rows")) {
                foundEasyRowsError = true;
                break;
            }
        }
        assertTrue(foundEasyRowsError, "Expected an error message for invalid rows on Easy difficulty.");

        verify(request).setAttribute(eq("config"), any());
        verify(dispatcher).forward(request, response);
        verify(response, never()).sendRedirect(anyString());
        verifyNoInteractions(daoMock);
        sysout("OK    [UC-15][15.2.1] Forward config.jsp, không ghi DB");
    }

    @Test
    @DisplayName("TC-UC15-05 - UC-15 / 15.2.1 + BR2: cols ngoài [5..50] -> forward config.jsp + errors")
    void doPost_invalidCols_shouldForwardWithErrors() throws Exception {
    	sysout("UC-15 / 15.2.1 + BR2: cols ngoài [5..50] -> forward config.jsp + errors");
        /**
         * Test Data (Input)
         * - Easy: rows=9, cols=60, mines=10 (cols vi phạm BR2)
         * - Medium: rows=16, cols=16, mines=40
         * - Hard: rows=16, cols=30, mines=99
         *
         * Business Rule áp dụng
         * - [UC-15][BR2] cols trong [5..50]
         *
         * Process
         * - [UC-15][15.1.3] POST /admin/config.
         * - [UC-15][15.2.1] Dữ liệu không hợp lệ: forward lại config.jsp kèm errors.
         *
         * Expected Output
         * - errors có chứa thông báo liên quan cols của Easy.
         * - Không gọi dao.updateConfig(...).
         */
        // Setup: tạo servlet + mock DAO/request/response/dispatcher.
        AdminConfigServlet servlet = new AdminConfigServlet();

        GameConfigDAO daoMock = mock(GameConfigDAO.class);
        injectGameConfigDao(servlet, daoMock);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getRequestDispatcher("/WEB-INF/views/admin/config.jsp")).thenReturn(dispatcher);

        // [UC-15][15.1.3] Input từ form (cols sai).
        when(request.getParameter("easy_rows")).thenReturn("9");
        when(request.getParameter("easy_cols")).thenReturn("60");
        when(request.getParameter("easy_mines")).thenReturn("10");

        when(request.getParameter("medium_rows")).thenReturn("16");
        when(request.getParameter("medium_cols")).thenReturn("16");
        when(request.getParameter("medium_mines")).thenReturn("40");

        when(request.getParameter("hard_rows")).thenReturn("16");
        when(request.getParameter("hard_cols")).thenReturn("30");
        when(request.getParameter("hard_mines")).thenReturn("99");

        // [UC-15][15.1.3] Thực thi xử lý POST /admin/config.
        sysout("START [UC-15][15.1.3] POST /admin/config (invalid cols)");
        servlet.doPost(request, response);
        sysout("OK    [UC-15][15.1.3] Servlet xử lý doPost");

        ArgumentCaptor<List<String>> errorsCaptor = stringListCaptor();
        sysout("START [UC-15][15.2.1] Validate fail -> errors + forward");
        verify(request).setAttribute(eq("errors"), errorsCaptor.capture());

        List<String> errors = errorsCaptor.getValue();
        assertNotNull(errors);
        assertFalse(errors.isEmpty());

        boolean foundEasyColsError = false;
        for (String error : errors) {
            if (error == null) {
                continue;
            }
            String lower = error.toLowerCase();
            if (error.contains("Easy") && lower.contains("cols")) {
                foundEasyColsError = true;
                break;
            }
        }
        assertTrue(foundEasyColsError, "Expected an error message for invalid cols on Easy difficulty.");

        verify(request).setAttribute(eq("config"), any());
        verify(dispatcher).forward(request, response);
        verify(response, never()).sendRedirect(anyString());
        verifyNoInteractions(daoMock);
        sysout("OK    [UC-15][15.2.1] Forward config.jsp, không ghi DB");
    }

    @Test
    @DisplayName("TC-UC15-06 - UC-15 / 15.1.3-15.1.4: config hợp lệ -> updateConfig + clear cache + redirect")
    void doPost_validConfig_shouldUpdateAndRedirect() throws Exception {
    	sysout("UC-15 / 15.1.3-15.1.4: config hợp lệ -> updateConfig + clear cache + redirect");
        /**
         * Test Data (Input)
         * - Session uid (adminUid): "admin_uid_1"
         * - ContextPath: "/minesweeper"
         * - Easy: rows=9, cols=9, mines=10
         * - Medium: rows=16, cols=16, mines=40
         * - Hard: rows=16, cols=30, mines=99
         *
         * Process
         * - [UC-15][15.1.3] Gọi doPost(request, response) để mô phỏng POST /admin/config với dữ liệu hợp lệ.
         * - [UC-15][15.1.4] Servlet build GameConfig, set updatedAt/updatedBy,
         *   gọi dao.updateConfig(config, adminUid), clear cache và redirect /admin/config.
         *
         * Expected Output
         * - daoMock.updateConfig(GameConfig, "admin_uid_1") được gọi.
         * - GameConfig truyền vào có easy/medium/hard đúng giá trị input và có updatedAt/updatedBy.
         * - servletContext.removeAttribute("gameConfigCache") và removeAttribute("gameConfigCacheTime") được gọi.
         * - response.sendRedirect("/minesweeper/admin/config") được gọi.
         */
        // Setup: tạo servlet + mock DAO/request/response/session/context.
        AdminConfigServlet servlet = new AdminConfigServlet();

        GameConfigDAO daoMock = mock(GameConfigDAO.class);
        injectGameConfigDao(servlet, daoMock);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        ServletContext context = mock(ServletContext.class);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("uid")).thenReturn("admin_uid_1");
        when(request.getServletContext()).thenReturn(context);
        when(request.getContextPath()).thenReturn("/minesweeper");

        // [UC-15][15.1.3] Admin nhập dữ liệu hợp lệ trên form (Easy/Medium/Hard).
        when(request.getParameter("easy_rows")).thenReturn("9");
        when(request.getParameter("easy_cols")).thenReturn("9");
        when(request.getParameter("easy_mines")).thenReturn("10");

        when(request.getParameter("medium_rows")).thenReturn("16");
        when(request.getParameter("medium_cols")).thenReturn("16");
        when(request.getParameter("medium_mines")).thenReturn("40");

        when(request.getParameter("hard_rows")).thenReturn("16");
        when(request.getParameter("hard_cols")).thenReturn("30");
        when(request.getParameter("hard_mines")).thenReturn("99");

        // [UC-15][15.1.3] Thực thi xử lý POST /admin/config.
        sysout("START [UC-15][15.1.3] POST /admin/config (valid)");
        servlet.doPost(request, response);
        sysout("OK    [UC-15][15.1.3] Servlet xử lý doPost");

        // [UC-15][15.1.4] Backend validate OK, set updatedAt/updatedBy và gọi DAO.updateConfig để lưu.
        sysout("START [UC-15][15.1.4] updateConfig + clear cache + redirect");
        ArgumentCaptor<GameConfig> configCaptor = ArgumentCaptor.forClass(GameConfig.class);
        verify(daoMock, times(1)).updateConfig(configCaptor.capture(), eq("admin_uid_1"));
        GameConfig config = configCaptor.getValue();
        assertNotNull(config);
        assertNotNull(config.getEasy());
        assertNotNull(config.getMedium());
        assertNotNull(config.getHard());
        assertNotNull(config.getUpdatedAt(), "updatedAt must be set in (15.1.4) before saving.");
        assertEquals("admin_uid_1", config.getUpdatedBy(), "updatedBy must be set to admin uid in (15.1.4).");

        assertEquals(9, config.getEasy().getRows());
        assertEquals(9, config.getEasy().getCols());
        assertEquals(10, config.getEasy().getMines());

        assertEquals(16, config.getMedium().getRows());
        assertEquals(16, config.getMedium().getCols());
        assertEquals(40, config.getMedium().getMines());

        assertEquals(16, config.getHard().getRows());
        assertEquals(30, config.getHard().getCols());
        assertEquals(99, config.getHard().getMines());

        // [UC-15][15.1.4] Clear cache để gameplay/route khác dùng cấu hình mới.
        verify(context, times(1)).removeAttribute("gameConfigCache");
        verify(context, times(1)).removeAttribute("gameConfigCacheTime");

        // [UC-15][15.1.4] Redirect kết thúc luồng cấu hình.
        verify(response, times(1)).sendRedirect("/minesweeper/admin/config");
        sysout("OK    [UC-15][15.1.4] Lưu config + clear cache + redirect thành công");
    }

    @Test
    @DisplayName("TC-UC15-04 - UC-15 / 15.1.4: lỗi DAO (ExecutionException) -> sendError 500")
    void doPost_daoThrowsExecutionException_shouldSendError500() throws Exception {
    	sysout("UC-15 / 15.1.4: lỗi DAO (ExecutionException) -> sendError 500");
        /**
         * Test Data (Input)
         * - Session uid (adminUid): "admin_uid_1"
         * - Easy: 9x9 mines=10, Medium: 16x16 mines=40, Hard: 16x30 mines=99 (hợp lệ)
         * - DAO.updateConfig(...) ném ExecutionException (mô phỏng lỗi Firestore)
         *
         * Process
         * - [UC-15][15.1.3] POST /admin/config với dữ liệu hợp lệ.
         * - [UC-15][15.1.4] Khi DAO lỗi, servlet catch và trả HTTP 500.
         *
         * Expected Output
         * - response.sendError(500) được gọi.
         * - Không redirect.
         */
        // Setup: tạo servlet + mock DAO/request/response/session/context.
        AdminConfigServlet servlet = new AdminConfigServlet();

        GameConfigDAO daoMock = mock(GameConfigDAO.class);
        injectGameConfigDao(servlet, daoMock);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        ServletContext context = mock(ServletContext.class);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("uid")).thenReturn("admin_uid_1");
        when(request.getServletContext()).thenReturn(context);
        when(request.getContextPath()).thenReturn("/minesweeper");

        when(request.getParameter("easy_rows")).thenReturn("9");
        when(request.getParameter("easy_cols")).thenReturn("9");
        when(request.getParameter("easy_mines")).thenReturn("10");

        when(request.getParameter("medium_rows")).thenReturn("16");
        when(request.getParameter("medium_cols")).thenReturn("16");
        when(request.getParameter("medium_mines")).thenReturn("40");

        when(request.getParameter("hard_rows")).thenReturn("16");
        when(request.getParameter("hard_cols")).thenReturn("30");
        when(request.getParameter("hard_mines")).thenReturn("99");

        doThrow(new ExecutionException(new RuntimeException("firestore down")))
                .when(daoMock)
                .updateConfig(any(), eq("admin_uid_1"));

        // [UC-15][15.1.3] Thực thi xử lý POST /admin/config.
        sysout("START [UC-15][15.1.3] POST /admin/config (DAO throws ExecutionException)");
        servlet.doPost(request, response);
        sysout("OK    [UC-15][15.1.3] Servlet xử lý doPost");

        // [UC-15][15.1.4] Lỗi DB -> HTTP 500.
        sysout("START [UC-15][15.1.4] Expect HTTP 500");
        verify(response, times(1)).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(response, never()).sendRedirect(anyString());
        sysout("OK    [UC-15][15.1.4] HTTP 500 returned");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static ArgumentCaptor<List<String>> stringListCaptor() {
        return (ArgumentCaptor) ArgumentCaptor.forClass(List.class);
    }

    private static void sysout(String message) {
        System.out.println(message);
    }

    private static void injectGameConfigDao(AdminConfigServlet servlet, GameConfigDAO daoMock) throws Exception {
        Field field = AdminConfigServlet.class.getDeclaredField("gameConfigDAO");
        field.setAccessible(true);
        field.set(servlet, daoMock);
    }
}
