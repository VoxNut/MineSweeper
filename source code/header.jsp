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
            logoutBtn.addEventListener("click", async () => {
                try {
                    await fetch("<%= _ctx %>/auth/logout", { method: "POST" });
                    window.location.href = "<%= _ctx %>/login";
                } catch (err) {
                    console.error("Logout failed:", err);
                    alert("Logout failed");
                }
            });
        }
    });
</script>
