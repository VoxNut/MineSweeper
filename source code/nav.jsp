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
        logoutBtn.addEventListener('click', async function () {
            try {
                await fetch('<%= request.getContextPath() %>/auth/logout', { method: 'POST' });
            } finally {
                window.location.href = '<%= request.getContextPath() %>/login';
            }
        });
    }());
</script>