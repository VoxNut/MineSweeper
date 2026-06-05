<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Minesweeper - Login</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css">
    <style>
        .auth-section { margin-top: 20px; border-top: 1px solid var(--border); padding-top: 18px; }
        .mode-switcher { display: flex; gap: 10px; margin-bottom: 14px; }
        .mode-switcher .btn { flex: 1; }
        .mode-shell {
            display: grid;
            overflow: hidden;
            transition: min-height 0.3s ease;
        }
        .mode-panel {
            grid-area: 1 / 1;
            display: flex;
            flex-direction: column;
            gap: 10px;
            opacity: 0;
            pointer-events: none;
            transform: translateY(10px);
            transition: opacity 0.28s ease, transform 0.28s ease;
        }
        .mode-shell[data-mode="signin"] { min-height: 348px; }
        .mode-shell[data-mode="register"] { min-height: 455px; }
        .mode-shell[data-mode="signin"] .mode-panel[data-mode-panel="signin"],
        .mode-shell[data-mode="register"] .mode-panel[data-mode-panel="register"] {
            opacity: 1;
            pointer-events: auto;
            transform: translateY(0);
        }
        .auth-section input { width: 100%; padding: 10px; border: 1px solid var(--border); border-radius: 8px; font-family: inherit; }
        .helper-text { margin: 0 0 10px; color: var(--muted); font-size: 0.92rem; }
        .field-error { min-height: 1em; margin: -2px 0 0; color: var(--danger); font-size: 0.82rem; }
        .text-link {
            display: inline-flex;
            align-self: flex-start;
            padding: 0;
            background: none;
            border: 0;
            color: var(--accent-dark);
            font: inherit;
            cursor: pointer;
            text-decoration: underline;
            text-underline-offset: 2px;
        }
    </style>
</head>
<body data-context-path="<%= request.getContextPath() %>">
<div class="page center">
    <div class="card cozy">
        <div class="stack">
            <div>
                <h1 class="title">Minesweeper</h1>
                <p class="muted">A soft, cozy board to play and track your scores.</p>
                <%
                    String error = request.getParameter("error");
                    if ("blocked".equals(error)) {
                %>
                    <p style="color: var(--danger); font-weight: bold;">Your account is locked</p>
                <% } %>
            </div>
            <button id="login-btn" class="btn primary full">Sign in with Google</button>
            <div class="links">
                <a class="btn ghost" href="<%= request.getContextPath() %>/leaderboard">View leaderboard</a>
            </div>

            <div class="auth-section">
                <div class="mode-switcher">
                    <button id="mode-signin-btn" class="btn primary" type="button">Sign in</button>
                    <button id="mode-register-btn" class="btn ghost" type="button">Register</button>
                </div>

                <div id="mode-shell" class="mode-shell" data-mode="signin">
                    <div class="mode-panel" data-mode-panel="signin">
                        <h4>Email account</h4>
                        <p class="helper-text">Sign in without Google using your email and password.</p>
                        <input type="email" id="signin-email" placeholder="you@example.com" />
                        <div id="signin-email-error" class="field-error"></div>
                        <input type="password" id="signin-password" placeholder="Password" />
                        <div id="signin-password-error" class="field-error"></div>
                        <button id="email-login-btn" class="btn primary full" type="button">Sign in with Email</button>
                        <button id="switch-to-register" class="text-link" type="button">No account yet? Create one</button>
                    </div>

                    <div class="mode-panel" data-mode-panel="register">
                        <h4>Create account</h4>
                        <p class="helper-text">Create a new player account with email and password.</p>
                        <input type="text" id="register-name" placeholder="Display name" />
                        <div id="register-name-error" class="field-error"></div>
                        <input type="email" id="register-email" placeholder="you@example.com" />
                        <div id="register-email-error" class="field-error"></div>
                        <input type="password" id="register-password" placeholder="Password" />
                        <div id="register-password-error" class="field-error"></div>
                        <input type="password" id="register-confirm-password" placeholder="Confirm password" />
                        <div id="register-confirm-password-error" class="field-error"></div>
                        <button id="email-register-btn" class="btn primary full" type="button">Create account</button>
                        <button id="switch-to-signin" class="text-link" type="button">Already have account? Sign in</button>
                    </div>
                </div>
            </div>

            <div class="auth-section">
                <h4>Forgot Password</h4>
                <p class="helper-text">Enter your account email and we will send a password reset link.</p>
                <input type="email" id="reset-email" placeholder="you@example.com" />
                <button id="reset-password-btn" class="btn ghost full" type="button">Send Reset Link</button>
            </div>
        </div>
    </div>
</div>
<script type="module" src="<%= request.getContextPath() %>/js/firebase-init.js"></script>
<script type="module" src="<%= request.getContextPath() %>/js/auth.js"></script>
<script type="module">
    import { showAlert } from "<%= request.getContextPath() %>/js/ui-alert.js";
    window.showAlert = showAlert;
</script>
</body>
</html>
