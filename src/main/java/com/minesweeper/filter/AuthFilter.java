package com.minesweeper.filter;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.minesweeper.dao.UserDAO;
import com.minesweeper.model.User;

public class AuthFilter implements Filter {
    private static final Logger LOGGER = Logger.getLogger(AuthFilter.class.getName());
    private final UserDAO userDAO = new UserDAO();

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String contextPath = httpRequest.getContextPath();
        String path = httpRequest.getRequestURI().substring(contextPath.length());

        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        if (session != null && session.getAttribute("uid") != null) {
            try {
                String uid = (String) session.getAttribute("uid");
                User user = userDAO.getUserByUid(uid);
                if (user != null && user.isBlocked()) {
                    session.invalidate();
                    httpResponse.sendRedirect(contextPath + "/login?error=blocked");
                    return;
                }
                chain.doFilter(request, response);
            } catch (ExecutionException | InterruptedException ex) {
                if (ex instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                LOGGER.log(Level.SEVERE, "Auth check failed", ex);
                httpResponse.sendRedirect(contextPath + "/login");
            }
        } else {
            httpResponse.sendRedirect(contextPath + "/login");
        }
    }

    private boolean isPublicPath(String path) {
        return path.equals("/")
                || path.equals("/home")
                || path.equals("/howtoplay")
                || path.equals("/login")
                || path.equals("/admin/login")
                || path.startsWith("/auth/")
                || path.equals("/leaderboard")
                || path.startsWith("/leaderboard/")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/sound/")
                || path.startsWith("/favicon");
    }

    @Override
    public void destroy() {
    }
}
