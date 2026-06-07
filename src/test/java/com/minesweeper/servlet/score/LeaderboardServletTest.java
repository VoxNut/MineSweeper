package com.minesweeper.servlet.score;

import com.minesweeper.service.LeaderboardService;
import org.junit.jupiter.api.BeforeEach;
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
public class LeaderboardServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private LeaderboardService leaderboardService;

    @InjectMocks
    private LeaderboardServlet leaderboardServlet;

    @BeforeEach
    public void setUp() {
        leaderboardServlet.leaderboardService = leaderboardService;
    }

    @Test
    public void testDoGet() throws Exception {
        when(leaderboardService.getTopScores(any())).thenReturn(new java.util.ArrayList<>());
        when(request.getRequestDispatcher("/WEB-INF/views/leaderboard.jsp")).thenReturn(dispatcher);
        
        leaderboardServlet.doGet(request, response);
        
        verify(request, times(1)).getRequestDispatcher("/WEB-INF/views/leaderboard.jsp");
        verify(dispatcher, times(1)).forward(request, response);
    }
}
