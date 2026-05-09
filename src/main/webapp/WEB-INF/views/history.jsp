<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.minesweeper.model.Score" %>
<%
    Integer totalGames = (Integer) request.getAttribute("totalGames");
    Integer winCount = (Integer) request.getAttribute("winCount");
    Integer lossCount = (Integer) request.getAttribute("lossCount");
    String winRateText = (String) request.getAttribute("winRateText");
    Integer totalScore = (Integer) request.getAttribute("totalScore");
    if (totalGames == null) {
        totalGames = 0;
    }
    if (winCount == null) {
        winCount = 0;
    }
    if (lossCount == null) {
        lossCount = 0;
    }
    if (winRateText == null) {
        winRateText = "0.0%";
    }
    if (totalScore == null) {
        totalScore = 0;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Minesweeper - History</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/includes/header.jsp" />
<div class="page">
    <div class="card wide cozy">
        <div class="row space">
            <div>
                <h1>My History</h1>
                <div class="score-line">
                    Total Score <span class="score-chip"><%= totalScore %></span>
                </div>
            </div>
            <a class="btn ghost" href="<%= request.getContextPath() %>/game">Back to Game</a>
        </div>
        <div class="stat-grid">
            <div class="stat-card">
                <span class="stat-label">Total Games</span>
                <span class="stat-value-lg"><%= totalGames %></span>
            </div>
            <div class="stat-card">
                <span class="stat-label">Score +</span>
                <span class="stat-value-lg"><%= winCount %></span>
            </div>
            <div class="stat-card">
                <span class="stat-label">Score -</span>
                <span class="stat-value-lg"><%= lossCount %></span>
            </div>
            <div class="stat-card">
                <span class="stat-label">Win Rate</span>
                <span class="stat-value-lg"><%= winRateText %></span>
            </div>
        </div>
        <table class="table">
            <thead>
            <tr>
                <th>Result</th>
                <th>Difficulty</th>
                <th>Time (s)</th>
                <th>Board</th>
                <th>Played At</th>
            </tr>
            </thead>
            <tbody>
            <%
                List<Score> scores = (List<Score>) request.getAttribute("scores");
                if (scores != null) {
                    for (Score score : scores) {
            %>
            <tr>
                <td><%= score.getResult() %></td>
                <td><%= score.getDifficulty() %></td>
                <td><%= score.getTimeSec() %></td>
                <td><%= score.getBoardRows() %>x<%= score.getBoardCols() %> (<%= score.getMineCount() %>)</td>
                <td><%= score.getPlayedAt() != null ? score.getPlayedAt().toDate() : "" %></td>
            </tr>
            <%
                    }
                }
            %>
            </tbody>
        </table>
    </div>
</div>
<jsp:include page="/WEB-INF/views/includes/footer.jsp" />
</body>
</html>
