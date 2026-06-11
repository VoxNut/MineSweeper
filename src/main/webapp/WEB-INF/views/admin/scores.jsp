<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.minesweeper.model.Score" %>
<%
    // [UC-14] 14.1.16 Đọc danh sách scores và các bộ lọc do AdminScoreServlet gắn vào request.
    List<Score> scoreList = (List<Score>) request.getAttribute("scores");
    if (scoreList == null) {
        scoreList = java.util.Collections.emptyList();
    }
    String difficulty = (String) request.getAttribute("difficulty");
    String uid = (String) request.getAttribute("uid");
    String flagged = (String) request.getAttribute("flagged");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Minesweeper - Admin Scores</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css">
    <style>
        .filter-bar {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
            gap: 10px;
            margin-bottom: 12px;
        }
        .flagged-row {
            background: rgba(216, 176, 138, 0.28);
        }
        .actions-col {
            display: flex;
            gap: 8px;
            flex-wrap: wrap;
        }
    </style>
</head>
<body>
<%@ include file="nav.jsp" %>
<div class="page">
    <div class="card wide cozy">
        <div class="row space">
            <div>
                <h1>Admin - Scores</h1>
                <p class="muted">Filter score history, flag fraud, or delete records.</p>
            </div>
            <span class="score-chip">Showing <%= scoreList.size() %> scores</span>
        </div>

        <%-- [UC-14] 14.1.2 / 14.1.16 Form GET cho phép lọc theo difficulty, uid và flagged. --%>
        <form method="get" action="<%= request.getContextPath() %>/admin/scores" class="filter-bar">
            <select name="difficulty">
                <option value="all" <%= difficulty == null || "all".equalsIgnoreCase(difficulty) ? "selected" : "" %>>All difficulties</option>
                <option value="easy" <%= "easy".equalsIgnoreCase(difficulty) ? "selected" : "" %>>Easy</option>
                <option value="medium" <%= "medium".equalsIgnoreCase(difficulty) ? "selected" : "" %>>Medium</option>
                <option value="hard" <%= "hard".equalsIgnoreCase(difficulty) ? "selected" : "" %>>Hard</option>
                <option value="custom" <%= "custom".equalsIgnoreCase(difficulty) ? "selected" : "" %>>Custom</option>
            </select>
            <input type="text" name="uid" placeholder="Filter by UID" value="<%= uid == null ? "" : uid %>">
            <select name="flagged">
                <option value="">All</option>
                <option value="true" <%= "true".equalsIgnoreCase(flagged) ? "selected" : "" %>>Flagged only</option>
                <option value="false" <%= "false".equalsIgnoreCase(flagged) ? "selected" : "" %>>Unflagged only</option>
            </select>
            <button class="btn primary" type="submit">Apply</button>
        </form>

        <table class="table">
            <thead>
            <tr>
                <th>Player</th>
                <th>Difficulty</th>
                <th>Time</th>
                <th>Result</th>
                <th>Date</th>
                <th>Flagged</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <%-- [UC-14] 14.1.16 / 14.1.18 Render danh sách score cùng trạng thái Flagged và thao tác quản trị. --%>
            <% for (Score score : scoreList) { %>
            <tr class="<%= score.isFlagged() ? "flagged-row" : "" %>">
                <td>
                    <div><strong><%= score.getDisplayName() %></strong></div>
                    <div class="muted"><%= score.getUid() %></div>
                </td>
                <td><%= score.getDifficulty() %></td>
                <td><%= score.getTimeSec() %>s</td>
                <td><%= score.getResult() %></td>
                <td><%= score.getPlayedAt() != null ? score.getPlayedAt().toDate() : "" %></td>
                <td><%= score.isFlagged() ? "Yes" : "No" %></td>
                <td>
                    <div class="actions-col">
                        <%-- [UC-14] 14.2.1 / 14.2.2 Gửi thao tác Flag hoặc Unflag cho score được chọn. --%>
                        <form method="post" action="<%= request.getContextPath() %>/admin/scores" style="margin:0;">
                            <input type="hidden" name="scoreId" value="<%= score.getScoreId() %>">
                            <input type="hidden" name="action" value="<%= score.isFlagged() ? "unflag" : "flag" %>">
                            <button class="btn" type="submit"><%= score.isFlagged() ? "Unflag" : "Flag as Fraud" %></button>
                        </form>
                        <%-- [UC-14] 14.2.1 / 14.2.2 Gửi thao tác Delete để xóa score khỏi lịch sử/điểm. --%>
                        <form method="post" action="<%= request.getContextPath() %>/admin/scores" style="margin:0;" onsubmit="return confirm('Delete this score?');">
                            <input type="hidden" name="scoreId" value="<%= score.getScoreId() %>">
                            <input type="hidden" name="action" value="delete">
                            <button class="btn" type="submit">Delete</button>
                        </form>
                    </div>
                </td>
            </tr>
            <% } %>
            </tbody>
        </table>
    </div>
</div>
<jsp:include page="/WEB-INF/views/includes/footer.jsp" />
</body>
</html>
