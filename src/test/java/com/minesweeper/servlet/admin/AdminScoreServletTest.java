package com.minesweeper.servlet.admin;

import com.google.cloud.firestore.Firestore;
import com.minesweeper.dao.ScoreDAO;
import com.minesweeper.model.Score;
import com.minesweeper.util.FirebaseUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminScoreServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private ScoreDAO scoreDAO;

    @Mock
    private Firestore firestore;

    private AdminScoreServlet adminScoreServlet;

    @BeforeEach
    public void setUp() throws Exception {
        try (MockedStatic<FirebaseUtil> firebaseUtilMock = Mockito.mockStatic(FirebaseUtil.class)) {
            firebaseUtilMock.when(FirebaseUtil::getFirestore).thenReturn(firestore);
            adminScoreServlet = new AdminScoreServlet();
        }

        Field scoreDAOField = AdminScoreServlet.class.getDeclaredField("scoreDAO");
        scoreDAOField.setAccessible(true);
        scoreDAOField.set(adminScoreServlet, scoreDAO);
    }

    @Test
    public void doGet_shouldForwardScoresPage() throws Exception {
        List<Score> scores = List.of(new Score());
        when(request.getParameter("difficulty")).thenReturn("easy");
        when(request.getParameter("uid")).thenReturn("user_001");
        when(request.getParameter("flagged")).thenReturn("true");
        when(scoreDAO.getAllScores("easy", "user_001", Boolean.TRUE)).thenReturn(scores);
        when(request.getRequestDispatcher("/WEB-INF/views/admin/scores.jsp")).thenReturn(dispatcher);

        adminScoreServlet.doGet(request, response);

        verify(scoreDAO, times(1)).getAllScores("easy", "user_001", Boolean.TRUE);
        verify(request, times(1)).setAttribute("scores", scores);
        verify(request, times(1)).setAttribute("difficulty", "easy");
        verify(request, times(1)).setAttribute("uid", "user_001");
        verify(request, times(1)).setAttribute("flagged", "true");
        verify(dispatcher, times(1)).forward(request, response);
    }

    @Test
    public void doGet_whenDaoFails_shouldSend500() throws Exception {
        when(scoreDAO.getAllScores(null, null, null))
                .thenThrow(new ExecutionException(new RuntimeException("Firestore error")));

        adminScoreServlet.doGet(request, response);

        verify(response, times(1)).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(request, never()).getRequestDispatcher("/WEB-INF/views/admin/scores.jsp");
    }

    @Test
    public void doPost_flag_shouldSetFlaggedTrue() throws Exception {
        when(request.getParameter("action")).thenReturn("flag");
        when(request.getParameter("scoreId")).thenReturn("score_001");
        when(request.getContextPath()).thenReturn("/minesweeper");

        adminScoreServlet.doPost(request, response);

        verify(scoreDAO, times(1)).flagScore("score_001", true);
        verify(response, times(1)).sendRedirect("/minesweeper/admin/scores");
    }

    @Test
    public void doPost_unflag_shouldSetFlaggedFalse() throws Exception {
        when(request.getParameter("action")).thenReturn("unflag");
        when(request.getParameter("scoreId")).thenReturn("score_001");
        when(request.getContextPath()).thenReturn("/minesweeper");

        adminScoreServlet.doPost(request, response);

        verify(scoreDAO, times(1)).flagScore("score_001", false);
        verify(response, times(1)).sendRedirect("/minesweeper/admin/scores");
    }

    @Test
    public void doPost_delete_shouldDeleteScore() throws Exception {
        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("scoreId")).thenReturn("score_001");
        when(request.getContextPath()).thenReturn("/minesweeper");

        adminScoreServlet.doPost(request, response);

        verify(scoreDAO, times(1)).deleteScore("score_001");
        verify(response, times(1)).sendRedirect("/minesweeper/admin/scores");
    }

    @Test
    public void doPost_emptyScoreId_shouldOnlyRedirect() throws Exception {
        when(request.getParameter("action")).thenReturn("flag");
        when(request.getParameter("scoreId")).thenReturn("");
        when(request.getContextPath()).thenReturn("/minesweeper");

        adminScoreServlet.doPost(request, response);

        verify(scoreDAO, never()).flagScore(anyString(), anyBoolean());
        verify(scoreDAO, never()).deleteScore(anyString());
        verify(response, times(1)).sendRedirect("/minesweeper/admin/scores");
    }
}
