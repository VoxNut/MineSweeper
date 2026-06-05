<%@ page contentType="text/html;charset=UTF-8" %>
<div class="topbar" style="margin-bottom: 18px; border-radius: 16px;">
    <div class="brand">
        <a href="<%= request.getContextPath() %>/admin" class="brand-title">Admin Panel</a>
        <span class="brand-sub">Minesweeper control room</span>
    </div>
    <div class="nav-links" style="flex-wrap: wrap; justify-content: flex-end;">
        <a class="btn ghost" href="<%= request.getContextPath() %>/admin/users">Users</a>
        <a class="btn ghost" href="<%= request.getContextPath() %>/admin/scores">Scores</a>
        <a class="btn ghost" href="<%= request.getContextPath() %>/admin/config">Game Config</a>
        <a class="btn ghost" href="<%= request.getContextPath() %>/game">Back to Game</a>
        <button id="admin-logout-btn" class="btn primary" type="button">Logout</button>
    </div>
</div>
<script>
    (function () {
        var logoutBtn = document.getElementById('admin-logout-btn');
        if (!logoutBtn) {
            return;
        }
        // [UC-05] 5.1.1 Người dùng (Admin) nhấn nút "Logout" trên thanh điều hướng.
        // [UC-05] 5.1.2 Frontend thực thi sự kiện JavaScript onClick tương ứng.
        logoutBtn.addEventListener('click', async function () {
            try {
                // [UC-05] 5.1.3 Frontend gửi request dạng HTTP POST đến endpoint API /auth/logout.
                await fetch('<%= request.getContextPath() %>/auth/logout', { method: 'POST' });
                // [UC-05] 5.1.7 Frontend tiếp nhận kết quả báo thành công.
            } catch (err) {
                // [UC-05] 5.3.1 / 5.3.2 Lỗi mạng và bắt exception.
                console.error("Admin logout failed:", err);
            } finally {
                // [UC-05] 5.1.8 / 5.1.9 (hoặc 5.3.3) Frontend chuyển hướng (redirect) về URL /login.
                window.location.href = '<%= request.getContextPath() %>/login';
            }
        });
    }());
</script>