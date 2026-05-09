package com.minesweeper.servlet.auth;

import com.google.gson.Gson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LogoutServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        // [UC-05] 5.1.4 Backend (LogoutServlet) tiếp nhận yêu cầu và truy xuất HttpSession hiện tại của người dùng.
        // [UC-05] 5.2.1 Backend gọi lấy session nhưng nhận về kết quả null (do hết hạn hoặc lỗi).
        HttpSession session = request.getSession(false);
        if (session != null) {
            // [UC-05] 5.1.5 Backend thực hiện gọi hàm session.invalidate() để xóa ngay lập tức toàn bộ dữ liệu phiên.
            session.invalidate();
        } else {
            // [UC-05] 5.2.2 Backend nhận thấy không có session nào, chủ động bỏ qua lệnh huỷ.
        }
        
        // [UC-05] 5.1.6 (hoặc 5.2.3) Backend phản hồi kết quả JSON {"success": true} kèm mã HTTP 200 OK.
        Map<String, Object> payload = new HashMap<>();
        payload.put("success", true);
        gson.toJson(payload, response.getWriter());
    }
}
