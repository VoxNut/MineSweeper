package com.minesweeper.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @InjectMocks
    private AuthFilter authFilter;

    @Test
    public void testFilterWithNoSession() throws Exception {
        when(request.getSession(false)).thenReturn(null);
        when(request.getContextPath()).thenReturn("/minesweeper");
        when(request.getRequestURI()).thenReturn("/minesweeper/game");
        
        authFilter.doFilter(request, response, chain);
        
        verify(response, times(1)).sendRedirect("/minesweeper/login");
        verify(chain, never()).doFilter(request, response);
    }
}
