<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.minesweeper.model.Score" %>
<%
    String difficulty = (String) request.getAttribute("difficulty");
    if (difficulty == null || difficulty.isEmpty()) {
        difficulty = "all";
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Minesweeper - Leaderboard</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/includes/header.jsp" />
<div class="page">
    <div class="card wide cozy">
        <div class="row space">
            <h1>Leaderboard</h1>
        </div>
        <form method="get" action="<%= request.getContextPath() %>/leaderboard" class="row">
            <label for="difficulty">Difficulty</label>
            <select id="difficulty" name="difficulty">
                <option value="all" <%= "all".equals(difficulty) ? "selected" : "" %>>All</option>
                <option value="easy" <%= "easy".equals(difficulty) ? "selected" : "" %>>Easy</option>
                <option value="medium" <%= "medium".equals(difficulty) ? "selected" : "" %>>Medium</option>
                <option value="hard" <%= "hard".equals(difficulty) ? "selected" : "" %>>Hard</option>
                <option value="custom" <%= "custom".equals(difficulty) ? "selected" : "" %>>Custom</option>
            </select>
            <button class="btn primary" type="submit">Filter</button>
        </form>

        <table class="table">
            <thead>
            <tr>
                <th>#</th>
                <th>Player</th>
                <th>Difficulty</th>
                <th>Time (s)</th>
                <th>Elo</th>
                <th>Played At</th>
            </tr>
            </thead>
            <tbody>
            <%
                List<Score> scores = (List<Score>) request.getAttribute("scores");
                int rank = 1;
                if (scores != null) {
                    for (Score score : scores) {
            %>
            <tr>
                <td><%= rank++ %></td>
                <td><%= score.getDisplayName() %></td>
                <td><%= score.getDifficulty() %></td>
                <td><%= score.getTimeSec() %></td>
                <td><%= score.getEloAfter() > 0 ? score.getEloAfter() : "-" %></td>
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
