<%@ page contentType="text/html;charset=UTF-8" %>
    <%@ page import="java.util.List" %>
        <%@ page import="com.minesweeper.model.Score" %>
            <% // UC-9 (9.1.8): Đọc tham số difficulty từ request attribute do LeaderboardServlet gán. // Nếu null hoặc
                rỗng, mặc định hiển thị "all" (tất cả mức độ khó) — UC-9, BR3. String difficulty=(String)
                request.getAttribute("difficulty"); if (difficulty==null || difficulty.isEmpty()) { difficulty="all" ; }
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
                            <%-- UC-9 (9.2.1 -> 9.2.2): Form lọc bảng xếp hạng theo mức độ khó.
                                Người dùng chọn giá trị trong dropdown và nhấn "Filter" để gửi
                                HTTP GET tới /leaderboard?difficulty={giá_trị}. --%>
                                <form method="get" action="<%= request.getContextPath() %>/leaderboard" class="row">
                                    <label for="difficulty">Difficulty</label>
                                    <%-- UC-9 (9.2.4): Mỗi option được đánh dấu "selected" nếu khớp với giá trị
                                        difficulty hiện tại — giữ lại trạng thái bộ lọc sau khi submit. --%>
                                        <select id="difficulty" name="difficulty">
                                            <option value="all" <%="all" .equals(difficulty) ? "selected" : "" %>>All
                                            </option>
                                            <option value="easy" <%="easy" .equals(difficulty) ? "selected" : "" %>>Easy
                                            </option>
                                            <option value="medium" <%="medium" .equals(difficulty) ? "selected" : "" %>
                                                >Medium</option>
                                            <option value="hard" <%="hard" .equals(difficulty) ? "selected" : "" %>>Hard
                                            </option>
                                            <option value="custom" <%="custom" .equals(difficulty) ? "selected" : "" %>
                                                >Custom</option>
                                        </select>
                                        <button class="btn primary" type="submit">Filter</button>
                                </form>

                                <%-- UC-9 (9.1.10): Bảng hiển thị kết quả xếp hạng. Các cột: Rank (#), Player,
                                    Difficulty, Time (s), Elo, Played At. --%>
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
                                            <% // UC-9 (9.1.9 -> 9.1.10): Đọc danh sách Score từ request attribute,
                                                // gán số thứ hạng bắt đầu từ 1 và lặp qua từng bản ghi để render hàng.
                                                // UC-9 (9.3.2): Nếu danh sách null hoặc rỗng, vòng lặp bị bỏ qua
                                                // và bảng sẽ hiển thị rỗng (không có hàng nào).
                                                List<Score> scores = (List<Score>) request.getAttribute("scores");
                                                        int rank = 1;
                                                        if (scores != null) {
                                                        for (Score score : scores) {
                                                        %>
                                                        <tr>
                                                            <td>
                                                                <%= rank++ %>
                                                            </td>
                                                            <td>
                                                                <%= score.getDisplayName() %>
                                                            </td>
                                                            <td>
                                                                <%= score.getDifficulty() %>
                                                            </td>
                                                            <td>
                                                                <%= score.getTimeSec() %>
                                                            </td>
                                                            <%-- UC-9 (9.1.11): Nếu eloAfter <=0, hiển thị "-" thay cho
                                                                giá trị Elo. --%>
                                                                <td>
                                                                    <%= score.getEloAfter()> 0 ? score.getEloAfter() :
                                                                        "-" %>
                                                                </td>
                                                                <td>
                                                                    <%= score.getPlayedAt() !=null ?
                                                                        score.getPlayedAt().toDate() : "" %>
                                                                </td>
                                                        </tr>
                                                        <% } } %>
                                        </tbody>
                                    </table>
                        </div>
                    </div>
                    <jsp:include page="/WEB-INF/views/includes/footer.jsp" />
                </body>

                </html>