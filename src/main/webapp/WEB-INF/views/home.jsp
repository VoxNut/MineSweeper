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
    <meta name="description" content="Minesweeper - A cozy, modern take on the classic puzzle game. Test your logic and strategy skills.">
    <title>Minesweeper - Cozy Board</title>
    <link rel="stylesheet" href="<%= ctx %>/css/main.css">
    <link rel="stylesheet" href="<%= ctx %>/css/home.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/includes/header.jsp" />

<main class="home-main">
    <section class="hero">
        <div class="hero-content">
            <h1 class="hero-title">
                <span class="hero-icon">
                    <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <circle cx="12" cy="12" r="6" fill="#8b5a36"/>
                        <line x1="12" y1="3" x2="12" y2="6" stroke="#8b5a36" stroke-width="2" stroke-linecap="round"/>
                        <line x1="12" y1="18" x2="12" y2="21" stroke="#8b5a36" stroke-width="2" stroke-linecap="round"/>
                        <line x1="3" y1="12" x2="6" y2="12" stroke="#8b5a36" stroke-width="2" stroke-linecap="round"/>
                        <line x1="18" y1="12" x2="21" y2="12" stroke="#8b5a36" stroke-width="2" stroke-linecap="round"/>
                        <line x1="5.6" y1="5.6" x2="7.8" y2="7.8" stroke="#8b5a36" stroke-width="2" stroke-linecap="round"/>
                        <line x1="16.2" y1="16.2" x2="18.4" y2="18.4" stroke="#8b5a36" stroke-width="2" stroke-linecap="round"/>
                        <line x1="5.6" y1="18.4" x2="7.8" y2="16.2" stroke="#8b5a36" stroke-width="2" stroke-linecap="round"/>
                        <line x1="16.2" y1="7.8" x2="18.4" y2="5.6" stroke="#8b5a36" stroke-width="2" stroke-linecap="round"/>
                        <circle cx="10" cy="10.5" r="1.8" fill="#b0784f"/>
                    </svg>
                </span>
                Minesweeper
            </h1>
            <p class="hero-subtitle">A cozy, modern take on the timeless classic</p>
            <p class="hero-desc">
                Navigate the board, uncover safe tiles, and flag hidden mines.
                Test your logic, sharpen your instincts, and climb the leaderboard.
            </p>
            <div class="hero-actions">
                <% if (loggedIn) { %>
                <a href="<%= ctx %>/game" class="btn primary btn-lg">Play Now</a>
                <% } else { %>
                <a href="<%= ctx %>/login" class="btn primary btn-lg">Login to Play</a>
                <% } %>
                <a href="<%= ctx %>/howtoplay" class="btn ghost btn-lg">Learn the Rules</a>
            </div>
        </div>
        <div class="hero-visual">
            <div class="mini-board" aria-hidden="true">
                <div class="mini-cell revealed">1</div>
                <div class="mini-cell revealed">2</div>
                <div class="mini-cell"></div>
                <div class="mini-cell"></div>
                <div class="mini-cell revealed"></div>
                <div class="mini-cell revealed">1</div>
                <div class="mini-cell flagged">
                    <svg viewBox="0 0 24 24" fill="none"><path d="M5 21V4" stroke="#8B4513" stroke-width="2.2" stroke-linecap="round"/><path d="M5 4L17 9L5 14Z" fill="#a45b4a" stroke="#8b5a36" stroke-width="0.5"/></svg>
                </div>
                <div class="mini-cell"></div>
                <div class="mini-cell revealed">1</div>
                <div class="mini-cell revealed">1</div>
                <div class="mini-cell revealed">2</div>
                <div class="mini-cell"></div>
                <div class="mini-cell revealed"></div>
                <div class="mini-cell revealed"></div>
                <div class="mini-cell revealed">1</div>
                <div class="mini-cell"></div>
            </div>
        </div>
    </section>

    <section class="features">
        <h2 class="section-title">Why You'll Love It</h2>
        <div class="feature-grid">
            <div class="feature-card">
                <div class="feature-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><circle cx="12" cy="12" r="6"/><circle cx="12" cy="12" r="2"/></svg>
                </div>
                <h3>Classic Gameplay</h3>
                <p>The same addictive logic puzzle you know and love — left-click to reveal, right-click to flag.</p>
            </div>
            <div class="feature-card">
                <div class="feature-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke-linecap="round" stroke-linejoin="round"><line x1="4" y1="21" x2="4" y2="14"/><line x1="4" y1="10" x2="4" y2="3"/><line x1="12" y1="21" x2="12" y2="12"/><line x1="12" y1="8" x2="12" y2="3"/><line x1="20" y1="21" x2="20" y2="16"/><line x1="20" y1="12" x2="20" y2="3"/><line x1="1" y1="14" x2="7" y2="14"/><line x1="9" y1="8" x2="15" y2="8"/><line x1="17" y1="16" x2="23" y2="16"/></svg>
                </div>
                <h3>Multiple Difficulties</h3>
                <p>Choose from Easy, Medium, Hard, or create your own Custom board with up to 200 mines.</p>
            </div>
            <div class="feature-card">
                <div class="feature-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke-linecap="round" stroke-linejoin="round"><path d="M6 9H4.5a2.5 2.5 0 0 1 0-5C7 4 7 7 7 7"/><path d="M18 9h1.5a2.5 2.5 0 0 0 0-5C17 4 17 7 17 7"/><path d="M4 22h16"/><path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22"/><path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22"/><path d="M18 2H6v7a6 6 0 0 0 12 0V2Z"/></svg>
                </div>
                <h3>Leaderboard</h3>
                <p>Compete with other players for the fastest solve times and climb the global rankings.</p>
            </div>
            <div class="feature-card">
                <div class="feature-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke-linecap="round" stroke-linejoin="round"><path d="M3 3v18h18"/><path d="M18 17V9"/><path d="M13 17V5"/><path d="M8 17v-3"/></svg>
                </div>
                <h3>Game History</h3>
                <p>Track your progress over time — review past games, win rates, and personal best times.</p>
            </div>
        </div>
    </section>

    <section class="cta-section">
        <div class="cta-card">
            <h2>Ready to Sweep?</h2>
            <p>Jump into a game and prove your skills on the cozy board.</p>
            <% if (loggedIn) { %>
            <a href="<%= ctx %>/game" class="btn primary btn-lg">Start Playing</a>
            <% } else { %>
            <a href="<%= ctx %>/login" class="btn primary btn-lg">Get Started</a>
            <% } %>
        </div>
    </section>
</main>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />
</body>
</html>
