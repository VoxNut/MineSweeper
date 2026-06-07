<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.cloud.Timestamp" %>
<%@ page import="com.minesweeper.model.GameConfig" %>
<%
    GameConfig gameConfig = (GameConfig) request.getAttribute("config");
    if (gameConfig == null) {
        gameConfig = GameConfig.defaultConfig();
    }
    List<String> errors = (List<String>) request.getAttribute("errors");
    GameConfig.DifficultyConfig easy = gameConfig.getEasy();
    GameConfig.DifficultyConfig medium = gameConfig.getMedium();
    GameConfig.DifficultyConfig hard = gameConfig.getHard();
    String updatedByDisplay = (String) request.getAttribute("updatedByDisplay");
    if (updatedByDisplay == null || updatedByDisplay.isEmpty()) {
        updatedByDisplay = gameConfig.getUpdatedBy();
    }
    String updatedAtText = "Never";
    if (gameConfig.getUpdatedAt() != null) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        updatedAtText = fmt.format(gameConfig.getUpdatedAt().toDate());
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Minesweeper - Game Config</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css">
    <style>
        .config-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
            gap: 14px;
            margin-top: 16px;
        }
        .config-box {
            border: 1px solid var(--border);
            border-radius: 16px;
            background: var(--card);
            padding: 16px;
        }
        .config-box input {
            width: 100%;
            margin-bottom: 10px;
        }
        .preview {
            font-size: 0.95rem;
            color: var(--muted);
            margin-top: 6px;
        }
        .error-box {
            margin: 10px 0 16px;
            padding: 12px 14px;
            background: rgba(164, 91, 74, 0.12);
            border: 1px solid rgba(164, 91, 74, 0.25);
            border-radius: 14px;
            color: var(--danger);
        }
    </style>
</head>
<body>
<%@ include file="nav.jsp" %>
<div class="page">
    <div class="card wide cozy">
        <div class="row space">
            <div>
                <h1>Game Config</h1>
                <p class="muted">Adjust the default board sizes and mine counts for standard difficulties.</p>
            </div>
            <div class="score-chip">Last updated by <%= updatedByDisplay == null || updatedByDisplay.isEmpty() ? "system" : updatedByDisplay %> on <%= updatedAtText %></div>
        </div>

        <% if (errors != null && !errors.isEmpty()) { %>
            <div class="error-box">
                <% for (String error : errors) { %>
                    <div><%= error %></div>
                <% } %>
            </div>
        <% } %>

        <form method="post" action="<%= request.getContextPath() %>/admin/config" class="stack">
            <div class="config-grid">
                <div class="config-box">
                    <h3>Easy</h3>
                    <label>Rows</label>
                    <input type="number" name="easy_rows" min="5" max="30" value="<%= easy.getRows() %>">
                    <label>Cols</label>
                    <input type="number" name="easy_cols" min="5" max="50" value="<%= easy.getCols() %>">
                    <label>Mines</label>
                    <input type="number" name="easy_mines" min="1" value="<%= easy.getMines() %>">
                    <div class="preview">Board: <span id="easy-preview"></span></div>
                </div>
                <div class="config-box">
                    <h3>Medium</h3>
                    <label>Rows</label>
                    <input type="number" name="medium_rows" min="5" max="30" value="<%= medium.getRows() %>">
                    <label>Cols</label>
                    <input type="number" name="medium_cols" min="5" max="50" value="<%= medium.getCols() %>">
                    <label>Mines</label>
                    <input type="number" name="medium_mines" min="1" value="<%= medium.getMines() %>">
                    <div class="preview">Board: <span id="medium-preview"></span></div>
                </div>
                <div class="config-box">
                    <h3>Hard</h3>
                    <label>Rows</label>
                    <input type="number" name="hard_rows" min="5" max="30" value="<%= hard.getRows() %>">
                    <label>Cols</label>
                    <input type="number" name="hard_cols" min="5" max="50" value="<%= hard.getCols() %>">
                    <label>Mines</label>
                    <input type="number" name="hard_mines" min="1" value="<%= hard.getMines() %>">
                    <div class="preview">Board: <span id="hard-preview"></span></div>
                </div>
            </div>
            <button type="submit" class="btn primary" style="margin-top: 10px;">Save Configuration</button>
        </form>
    </div>
</div>
<script>
    function bindPreview(prefix) {
        const rows = document.querySelector('input[name="' + prefix + '_rows"]');
        const cols = document.querySelector('input[name="' + prefix + '_cols"]');
        const mines = document.querySelector('input[name="' + prefix + '_mines"]');
        const preview = document.getElementById(prefix + '-preview');
        const render = () => {
            const r = parseInt(rows.value || '0', 10);
            const c = parseInt(cols.value || '0', 10);
            const m = parseInt(mines.value || '0', 10);
            const density = r > 0 && c > 0 ? ((m / (r * c)) * 100).toFixed(1) : '0.0';
            preview.textContent = r + ' × ' + c + ' | Mines: ' + m + ' | Mine density: ' + density + '%';
        };
        rows.addEventListener('input', render);
        cols.addEventListener('input', render);
        mines.addEventListener('input', render);
        render();
    }
    bindPreview('easy');
    bindPreview('medium');
    bindPreview('hard');
</script>
<jsp:include page="/WEB-INF/views/includes/footer.jsp" />
</body>
</html>