<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.minesweeper.model.User" %>
<%
    List<User> userList = (List<User>) request.getAttribute("users");
    if (userList == null) {
        userList = java.util.Collections.emptyList();
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Minesweeper - Admin Users</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css">
    <style>
        .badge {
            display: inline-flex;
            align-items: center;
            padding: 4px 10px;
            border-radius: 999px;
            font-size: 0.78rem;
            font-weight: 700;
        }
        .badge.active { background: rgba(123, 138, 90, 0.16); color: var(--success); }
        .badge.blocked { background: rgba(164, 91, 74, 0.16); color: var(--danger); }
        .user-avatar {
            width: 34px;
            height: 34px;
            border-radius: 50%;
            object-fit: cover;
            border: 1px solid var(--border);
            vertical-align: middle;
            margin-right: 8px;
        }
        .toolbar {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
            margin-bottom: 12px;
        }
        .toolbar input {
            min-width: 260px;
        }
        .pagination {
            display: flex;
            gap: 8px;
            justify-content: flex-end;
            margin-top: 12px;
            flex-wrap: wrap;
        }
        .pagination button {
            min-width: 42px;
        }
    </style>
</head>
<body>
<%@ include file="nav.jsp" %>
<div class="page">
    <div class="card wide cozy">
        <div class="row space">
            <div>
                <h1>Admin - Users</h1>
                <p class="muted">Search users, block/unblock accounts, and change roles.</p>
            </div>
            <span class="score-chip">Total users: <%= userList.size() %></span>
        </div>

        <%
            String currentUid = session != null ? (String) session.getAttribute("uid") : null;
            String error = request.getParameter("error");
        %>
        <% if ("self-block".equals(error)) { %>
            <p style="color: var(--danger); font-weight: 700; margin-top: 0;">You cannot block your own admin account.</p>
        <% } %>

        <div class="toolbar">
            <input type="search" id="user-search" placeholder="Search by name or email">
        </div>

        <table class="table" id="users-table">
            <thead>
            <tr>
                <th>Avatar</th>
                <th>Display Name</th>
                <th>Email</th>
                <th>Role</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody id="users-body">
            <% for (User user : userList) { %>
            <tr data-search="<%= (user.getDisplayName() == null ? "" : user.getDisplayName()) + " " + (user.getEmail() == null ? "" : user.getEmail()) %>">
                <td>
                    <% if (user.getPhotoURL() != null && !user.getPhotoURL().isEmpty()) { %>
                        <img class="user-avatar" src="<%= user.getPhotoURL() %>" alt="avatar">
                    <% } else { %>
                        <span class="badge active">U</span>
                    <% } %>
                </td>
                <td><%= user.getDisplayName() %></td>
                <td><%= user.getEmail() %></td>
                <td><%= user.getRole() %></td>
                <td>
                    <% if (user.isBlocked()) { %>
                        <span class="badge blocked">Blocked</span>
                    <% } else { %>
                        <span class="badge active">Active</span>
                    <% } %>
                </td>
                <td>
                    <form method="post" action="<%= request.getContextPath() %>/admin/users" class="row actions" style="margin:0;">
                        <input type="hidden" name="uid" value="<%= user.getUid() %>">
                        <input type="hidden" name="action" value="<%= user.isBlocked() ? "unblock" : "block" %>">
                        <% if (currentUid != null && currentUid.equals(user.getUid()) && !user.isBlocked()) { %>
                            <button class="btn" type="submit" disabled title="You cannot block your own account">Block</button>
                        <% } else { %>
                            <button class="btn" type="submit"><%= user.isBlocked() ? "Unblock" : "Block" %></button>
                        <% } %>
                    </form>
                    <form method="post" action="<%= request.getContextPath() %>/admin/users" class="row actions" style="margin:8px 0 0;">
                        <input type="hidden" name="uid" value="<%= user.getUid() %>">
                        <input type="hidden" name="action" value="setRole">
                        <select name="role">
                            <option value="player" <%= "player".equalsIgnoreCase(user.getRole()) ? "selected" : "" %>>Player</option>
                            <option value="admin" <%= "admin".equalsIgnoreCase(user.getRole()) ? "selected" : "" %>>Admin</option>
                        </select>
                        <button class="btn primary" type="submit">Apply</button>
                    </form>
                </td>
            </tr>
            <% } %>
            </tbody>
        </table>

        <div class="pagination" id="users-pagination"></div>
    </div>
</div>
<script>
    (function () {
        const rowsPerPage = 20;
        const searchInput = document.getElementById('user-search');
        const rows = Array.from(document.querySelectorAll('#users-body tr'));
        const pagination = document.getElementById('users-pagination');
        let filteredRows = rows.slice();
        let currentPage = 1;

        function render() {
            const start = (currentPage - 1) * rowsPerPage;
            const end = start + rowsPerPage;
            rows.forEach(row => row.style.display = 'none');
            filteredRows.slice(start, end).forEach(row => row.style.display = 'table-row');
            const pageCount = Math.max(1, Math.ceil(filteredRows.length / rowsPerPage));
            pagination.innerHTML = '';
            for (let i = 1; i <= pageCount; i++) {
                const btn = document.createElement('button');
                btn.type = 'button';
                btn.className = 'btn ' + (i === currentPage ? 'primary' : 'ghost');
                btn.textContent = i;
                btn.addEventListener('click', () => {
                    currentPage = i;
                    render();
                });
                pagination.appendChild(btn);
            }
        }

        function applyFilter() {
            const term = searchInput.value.trim().toLowerCase();
            filteredRows = rows.filter(row => row.dataset.search.toLowerCase().includes(term));
            currentPage = 1;
            render();
        }

        searchInput.addEventListener('input', applyFilter);
        applyFilter();
    }());
</script>
<jsp:include page="/WEB-INF/views/includes/footer.jsp" />
</body>
</html>
