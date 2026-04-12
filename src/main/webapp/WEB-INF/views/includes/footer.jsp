<%@ page contentType="text/html;charset=UTF-8" %>
<%
    String _ctxF = request.getContextPath();
%>
<footer class="site-footer">
    <span>&copy; 2026 Minesweeper &mdash; Cozy Board</span>
    <div class="footer-links">
        <a href="<%= _ctxF %>/home">Home</a>
        <a href="<%= _ctxF %>/leaderboard">Leaderboard</a>
        <a href="<%= _ctxF %>/howtoplay">How to Play</a>
    </div>
</footer>
