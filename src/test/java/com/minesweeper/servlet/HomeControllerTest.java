package com.minesweeper.servlet;

import com.minesweeper.servlet.HomeServlet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HomeControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher dispatcher;

    @InjectMocks
    private HomeServlet homeServlet;

    @Test
    public void testDoGet() throws Exception {
        when(request.getRequestDispatcher("/WEB-INF/views/home.jsp")).thenReturn(dispatcher);
        
        homeServlet.doGet(request, response);
        
        verify(request, times(1)).getRequestDispatcher("/WEB-INF/views/home.jsp");
        verify(dispatcher, times(1)).forward(request, response);
    }
}
