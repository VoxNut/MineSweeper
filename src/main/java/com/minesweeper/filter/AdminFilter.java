package com.minesweeper.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AdminFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        if (path.equals("/admin/login")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);

        // [UC-13] 13.1.3 / 13.2.3 Với request /admin/users, kiểm tra session có thuộc admin hay không.
        if (session != null && "admin".equals(session.getAttribute("role"))) {
            // [UC-13] 13.1.4 Cho request admin hợp lệ đi tiếp đến AdminUserServlet.
            chain.doFilter(request, response);
            return;
        }
        // [UC-13] 13.2.1 Redirect người chưa đăng nhập hoặc không phải admin về /home.
        httpResponse.sendRedirect(httpRequest.getContextPath() + "/home");
    }

    @Override
    public void destroy() {
    }
}
