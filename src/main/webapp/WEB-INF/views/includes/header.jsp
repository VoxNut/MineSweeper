<%@ page contentType="text/html;charset=UTF-8" %>
<%
    String _displayName = null;
    String _photoURL = null;
    String _role = null;
    if (session != null) {
        _displayName = (String) session.getAttribute("displayName");
        _photoURL = (String) session.getAttribute("photoURL");
        _role = (String) session.getAttribute("role");
    }
    boolean _loggedIn = (_displayName != null);
    String _ctx = request.getContextPath();
%>
<header class="topbar">
    <div class="brand">
        <a href="<%= _ctx %>/home" class="brand-title">Minesweeper</a>
        <span class="brand-sub">cozy board</span>
    </div>
    <nav class="nav-links">
        <% if (_loggedIn) { %>
        <a href="<%= _ctx %>/game" class="btn ghost">Play</a>
        <a href="<%= _ctx %>/leaderboard" class="btn ghost">Leaderboard</a>
        <a href="<%= _ctx %>/history" class="btn ghost">History</a>
        <a href="<%= _ctx %>/howtoplay" class="btn ghost">How to Play</a>
        <% if ("admin".equals(_role)) { %>
        <a href="<%= _ctx %>/admin/users" class="btn ghost">Admin</a>
        <% } %>
        <div class="user">
            <% if (_photoURL != null && !_photoURL.isEmpty()) { %>
            <img class="avatar" src="<%= _photoURL %>" alt="avatar">
            <% } %>
            <span><%= _displayName %></span>
            <button id="logout-btn" class="btn ghost">Logout</button>
        </div>
        <% } else { %>
        <a href="<%= _ctx %>/howtoplay" class="btn ghost">How to Play</a>
        <a href="<%= _ctx %>/login" class="btn primary">Login</a>
        <% } %>
    </nav>
</header>

<script>
    document.addEventListener("DOMContentLoaded", function() {
        const logoutBtn = document.getElementById("logout-btn");
        if (logoutBtn) {
            // [UC-05] 5.1.1 Người dùng nhấn nút "Logout" trên thanh điều hướng.
            // [UC-05] 5.1.2 Frontend thực thi sự kiện JavaScript onClick tương ứng.
            logoutBtn.addEventListener("click", async () => {
                try {
                    // [UC-05] 5.1.3 Frontend gửi request dạng HTTP POST đến endpoint API /auth/logout.
                    await fetch("<%= _ctx %>/auth/logout", { method: "POST" });
                    // [UC-05] 5.1.7 Frontend tiếp nhận kết quả báo thành công.
                    // [UC-05] 5.1.8 / 5.1.9 Frontend chuyển hướng (redirect) về URL /login.
                    window.location.href = "<%= _ctx %>/login";
                } catch (err) {
                    // [UC-05] 5.3.1 Request API tới /auth/logout bị nghẽn (mạng lỗi, server tắt).
                    // [UC-05] 5.3.2 Frontend bắt lỗi (exception) trong khối catch.
                    console.error("Logout failed:", err);
                    // [UC-05] 5.3.3 Frontend in lỗi ra console hoặc gọi alert.
                    alert("Logout failed");
                }
            });
        }
    });
</script>
