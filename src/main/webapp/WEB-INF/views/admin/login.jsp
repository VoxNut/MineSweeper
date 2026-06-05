<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Minesweeper - Admin Login</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css">
    <style>
        .admin-login-card input { width: 100%; padding: 10px; margin-bottom: 10px; border: 1px solid var(--border); border-radius: 8px; font-family: inherit; }
        .helper-text { margin: 0 0 10px; color: var(--muted); font-size: 0.92rem; }
    </style>
</head>
<body data-context-path="<%= request.getContextPath() %>">
<div class="page center">
    <div class="card cozy admin-login-card">
        <div class="stack">
            <div>
                <h1 class="title">Admin Login</h1>
                <p class="muted">Use the admin account to manage users, scores, and game settings.</p>
            </div>
            <input type="email" id="admin-email" value="msadmin@minesweeper.local" readonly />
            <input type="password" id="admin-password" placeholder="Password" />
            <button id="admin-login-btn" class="btn primary full" type="button">Login as Admin</button>
            <a class="btn ghost full" href="<%= request.getContextPath() %>/login">Back to player login</a>
        </div>
    </div>
</div>
<script type="module" src="<%= request.getContextPath() %>/js/firebase-init.js"></script>
<script type="module" src="<%= request.getContextPath() %>/js/admin-auth.js"></script>
<script type="module">
    import { showAlert } from "<%= request.getContextPath() %>/js/ui-alert.js";
    window.showAlert = showAlert;
</script>
</body>
</html>