<%@ page contentType="text/html;charset=UTF-8" %>
<%
    String displayName = null;
    if (session != null) {
        displayName = (String) session.getAttribute("displayName");
    }
    boolean loggedIn = (displayName != null);
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Learn how to play Minesweeper - rules, controls, tips and strategies.">
    <title>How to Play - Minesweeper</title>
    <link rel="stylesheet" href="<%= ctx %>/css/main.css">
    <link rel="stylesheet" href="<%= ctx %>/css/home.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/includes/header.jsp" />

<main class="htp-main">
    <div class="htp-container">
        <h1 class="htp-title">How to Play</h1>
        <p class="htp-intro">Minesweeper is a classic logic puzzle. Your goal is to uncover all safe cells without detonating any hidden mines.</p>

        <section class="htp-section">
            <h2>
                <span class="htp-step-num">1</span>
                Objective
            </h2>
            <p>The board contains hidden mines. Your mission is to reveal every cell that is <strong>not</strong> a mine. If you reveal a mine — the game is over!</p>
        </section>

        <section class="htp-section">
            <h2>
                <span class="htp-step-num">2</span>
                Controls
            </h2>
            <div class="controls-grid">
                <div class="control-card">
                    <div class="control-icon">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke-linecap="round" stroke-linejoin="round"><path d="M9 2h6v6l3 3-4 4v5H10v-5L6 11l3-3V2z"/><line x1="12" y1="12" x2="12" y2="22"/></svg>
                    </div>
                    <div>
                        <strong>Left Click</strong>
                        <p>Reveal a cell. If the cell has no adjacent mines, a larger area will automatically open up.</p>
                    </div>
                </div>
                <div class="control-card">
                    <div class="control-icon">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke-linecap="round" stroke-linejoin="round"><path d="M4 15s1-1 4-1 5 2 8 2 4-1 4-1V3s-1 1-4 1-5-2-8-2-4 1-4 1z"/><line x1="4" y1="22" x2="4" y2="15"/></svg>
                    </div>
                    <div>
                        <strong>Right Click</strong>
                        <p>Place or remove a flag on a cell you suspect contains a mine. Flagged cells cannot be accidentally revealed.</p>
                    </div>
                </div>
            </div>
        </section>

        <section class="htp-section">
            <h2>
                <span class="htp-step-num">3</span>
                Reading the Numbers
            </h2>
            <p>When you reveal a cell, a number appears indicating how many of the <strong>8 surrounding cells</strong> contain mines.</p>
            <div class="number-examples">
                <div class="num-example">
                    <span class="num-cell" style="color: #7c6a57;">1</span>
                    <span>One adjacent mine</span>
                </div>
                <div class="num-example">
                    <span class="num-cell" style="color: #8b5a36;">2</span>
                    <span>Two adjacent mines</span>
                </div>
                <div class="num-example">
                    <span class="num-cell" style="color: #a45b4a;">3</span>
                    <span>Three adjacent mines</span>
                </div>
                <div class="num-example">
                    <span class="num-cell" style="color: #6b4226;">4+</span>
                    <span>Four or more nearby</span>
                </div>
            </div>
            <p>An empty revealed cell means <strong>zero</strong> adjacent mines — and its neighbors are auto-revealed.</p>
        </section>

        <section class="htp-section">
            <h2>
                <span class="htp-step-num">4</span>
                Difficulty Levels
            </h2>
            <div class="diff-table-wrap">
                <table class="table">
                    <thead>
                    <tr>
                        <th>Difficulty</th>
                        <th>Grid Size</th>
                        <th>Mines</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>Easy</td>
                        <td>9 &times; 9</td>
                        <td>10</td>
                    </tr>
                    <tr>
                        <td>Medium</td>
                        <td>16 &times; 16</td>
                        <td>40</td>
                    </tr>
                    <tr>
                        <td>Hard</td>
                        <td>16 &times; 30</td>
                        <td>99</td>
                    </tr>
                    <tr>
                        <td>Custom</td>
                        <td>Your choice</td>
                        <td>Your choice</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </section>

        <section class="htp-section">
            <h2>
                <span class="htp-step-num">5</span>
                Tips &amp; Strategy
            </h2>
            <ul class="tips-list">
                <li><strong>First click is always safe</strong> — mines are placed after your first reveal.</li>
                <li><strong>Use flags wisely</strong> — flag cells you are certain contain mines to avoid misclicks.</li>
                <li><strong>Work from edges</strong> — corners and edges have fewer neighbors, making them easier to deduce.</li>
                <li><strong>Count carefully</strong> — if a "1" cell only has one unrevealed neighbor, that neighbor is definitely a mine.</li>
                <li><strong>Speed matters</strong> — your time is recorded, so practice to climb the leaderboard!</li>
            </ul>
        </section>

        <div class="htp-cta">
            <% if (loggedIn) { %>
            <a href="<%= ctx %>/game" class="btn primary btn-lg">Start Playing</a>
            <% } else { %>
            <a href="<%= ctx %>/login" class="btn primary btn-lg">Login to Play</a>
            <% } %>
            <a href="<%= ctx %>/home" class="btn ghost btn-lg">Back to Home</a>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />
</body>
</html>

<!-- Step-by-step imagery guide mapping cards -->
