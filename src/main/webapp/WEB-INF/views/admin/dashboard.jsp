<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.Locale" %>
<%
    Integer totalUsers = (Integer) request.getAttribute("totalUsers");
    Integer totalGames = (Integer) request.getAttribute("totalGames");
    Integer flaggedScores = (Integer) request.getAttribute("flaggedScores");
    Integer wins = (Integer) request.getAttribute("wins");
    Integer losses = (Integer) request.getAttribute("losses");
    Integer easyGames = (Integer) request.getAttribute("easyGames");
    Integer mediumGames = (Integer) request.getAttribute("mediumGames");
    Integer hardGames = (Integer) request.getAttribute("hardGames");
    Integer customGames = (Integer) request.getAttribute("customGames");
    Integer newUsersToday = (Integer) request.getAttribute("newUsersToday");
    String winRate = (String) request.getAttribute("winRate");

    totalUsers = totalUsers == null ? 0 : totalUsers;
    totalGames = totalGames == null ? 0 : totalGames;
    flaggedScores = flaggedScores == null ? 0 : flaggedScores;
    wins = wins == null ? 0 : wins;
    losses = losses == null ? 0 : losses;
    easyGames = easyGames == null ? 0 : easyGames;
    mediumGames = mediumGames == null ? 0 : mediumGames;
    hardGames = hardGames == null ? 0 : hardGames;
    customGames = customGames == null ? 0 : customGames;
    newUsersToday = newUsersToday == null ? 0 : newUsersToday;
    winRate = winRate == null ? "0.0%" : winRate;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Minesweeper - Admin Dashboard</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css">
    <style>
        .metric-accent {
            display: block;
            margin-top: 6px;
            font-size: 0.85rem;
            color: var(--muted);
        }
        .chart-card {
            margin-top: 14px;
            background: var(--card);
            border: 1px solid var(--border);
            border-radius: 16px;
            padding: 18px;
            box-shadow: var(--shadow-soft);
        }
    </style>
</head>
<body>
<%@ include file="nav.jsp" %>
<div class="page">
    <div class="card wide cozy">
        <div class="row space">
            <div>
                <h1>Admin Dashboard</h1>
                <p class="muted">Overview of players, matches, and moderation status.</p>
            </div>
            <span class="score-chip">New users today: <%= newUsersToday %></span>
        </div>

        <div class="stat-grid">
            <div class="stat-card">
                <span class="stat-label">Total Users</span>
                <span class="stat-value-lg"><%= totalUsers %></span>
                <span class="metric-accent">Active accounts in Firestore</span>
            </div>
            <div class="stat-card">
                <span class="stat-label">Total Games</span>
                <span class="stat-value-lg"><%= totalGames %></span>
                <span class="metric-accent"><%= wins %> wins / <%= losses %> losses</span>
            </div>
            <div class="stat-card">
                <span class="stat-label">Win Rate</span>
                <span class="stat-value-lg"><%= winRate %></span>
                <span class="metric-accent">Across all recorded scores</span>
            </div>
            <div class="stat-card">
                <span class="stat-label">Flagged Scores</span>
                <span class="stat-value-lg"><%= flaggedScores %></span>
                <span class="metric-accent">Pending moderation review</span>
            </div>
        </div>

        <div class="chart-card">
            <div class="row space">
                <h2 style="margin: 0;">Games per Difficulty</h2>
                <div class="muted">Updated from the scores collection</div>
            </div>
            <canvas id="difficultyChart" height="120"></canvas>
        </div>

        <div class="row actions" style="margin-top: 16px;">
            <a class="btn primary" href="<%= request.getContextPath() %>/admin/users">Manage Users</a>
            <a class="btn primary" href="<%= request.getContextPath() %>/admin/scores">Manage Scores</a>
            <a class="btn primary" href="<%= request.getContextPath() %>/admin/config">Game Config</a>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
    const ctx = document.getElementById('difficultyChart');
    if (ctx) {
        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: ['Easy', 'Medium', 'Hard', 'Custom'],
                datasets: [{
                    label: 'Games',
                    data: [<%= easyGames %>, <%= mediumGames %>, <%= hardGames %>, <%= customGames %>],
                    backgroundColor: ['#b0784f', '#c99568', '#d8b08a', '#e7c8a7'],
                    borderRadius: 10
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { display: false }
                },
                scales: {
                    y: { beginAtZero: true, ticks: { precision: 0 } }
                }
            }
        });
    }
</script>
<jsp:include page="/WEB-INF/views/includes/footer.jsp" />
</body>
</html>