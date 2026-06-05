<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Minesweeper - Game</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/game.css">
</head>
<body data-context-path="<%= request.getContextPath() %>">
<jsp:include page="/WEB-INF/views/includes/header.jsp" />

<main class="game-shell">
    <section class="control-card">
        <div class="control-head">
            <div class="control-title">
                <p class="eyebrow">Session</p>
                <h1 class="section-title">Cozy Command</h1>
            </div>
        </div>

        <div class="control-block">
            <div class="row">
                <label for="difficulty">Difficulty</label>
                <select id="difficulty">
                    <option value="easy">Easy</option>
                    <option value="medium">Medium</option>
                    <option value="hard">Hard</option>
                    <option value="custom">Custom</option>
                </select>
            </div>
            <div class="custom" id="custom-fields">
                <label>Rows <input id="custom-rows" type="number" min="5" max="30" value="10"></label>
                <label>Cols <input id="custom-cols" type="number" min="5" max="30" value="10"></label>
                <label>Mines <input id="custom-mines" type="number" min="1" max="200" value="12"></label>
            </div>
        </div>

        <div class="control-block stat-grid">
            <div class="stat-pill">
                <span class="stat-label">Mines</span>
                <span id="mine-count" class="stat-value">0</span>
            </div>
            <div class="stat-pill">
                <span class="stat-label">Time</span>
                <span id="timer" class="stat-value">0</span>
                <span class="stat-unit">s</span>
            </div>
        </div>

        <div class="control-actions">
            <button id="new-game" class="btn primary">New Game</button>
            <a class="btn ghost" href="<%= request.getContextPath() %>/howtoplay">How to Play</a>
        </div>

        <div class="tip-card">
            <h3 class="tip-title">Quick Hints</h3>
            <ul class="tip-list">
                <li>Numbers show how many mines touch a cell.</li>
                <li>Right-click to place a flag.</li>
                <li>Clear every safe tile to win.</li>
            </ul>
        </div>
    </section>

    <section class="playfield">
        <div class="board-tray">
            <div class="board-meta">
                <span class="meta-pill">Classic board</span>
                <span class="meta-pill">Warm up and sweep</span>
            </div>
            <div class="board-frame">
                <div id="board" class="board"></div>
            </div>
        </div>
    </section>
</main>

<div id="overlay" class="overlay hidden">
    <div class="overlay-card">
        <h2 id="overlay-title">Game Over</h2>
        <button id="overlay-new" class="btn primary">Play Again</button>
        <a class="btn ghost" href="<%= request.getContextPath() %>/leaderboard">Leaderboard</a>
    </div>
</div>

<script type="module" src="<%= request.getContextPath() %>/js/game.js"></script>
</body>
</html>
