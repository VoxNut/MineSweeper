package com.minesweeper.servlet.admin;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.minesweeper.util.FirebaseUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminDashboardServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private Firestore firestore;

    @Mock
    private CollectionReference usersCollection;

    @Mock
    private CollectionReference scoresCollection;

    @Mock
    private ApiFuture<QuerySnapshot> usersFuture;

    @Mock
    private ApiFuture<QuerySnapshot> scoresFuture;

    @Mock
    private QuerySnapshot usersSnapshot;

    @Mock
    private QuerySnapshot scoresSnapshot;

    @InjectMocks
    private AdminDashboardServlet adminDashboardServlet;

    @Test
    public void testDoGet() throws Exception {
        try (MockedStatic<FirebaseUtil> firebaseUtilMock = Mockito.mockStatic(FirebaseUtil.class)) {
            firebaseUtilMock.when(FirebaseUtil::getFirestore).thenReturn(firestore);
            when(firestore.collection("users")).thenReturn(usersCollection);
            when(firestore.collection("scores")).thenReturn(scoresCollection);
            when(usersCollection.get()).thenReturn(usersFuture);
            when(scoresCollection.get()).thenReturn(scoresFuture);
            when(usersFuture.get()).thenReturn(usersSnapshot);
            when(scoresFuture.get()).thenReturn(scoresSnapshot);
            
            when(usersSnapshot.getDocuments()).thenReturn(Collections.emptyList());
            when(scoresSnapshot.getDocuments()).thenReturn(Collections.emptyList());
            when(usersSnapshot.size()).thenReturn(0);
            when(scoresSnapshot.size()).thenReturn(0);

            when(request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp")).thenReturn(dispatcher);

            adminDashboardServlet.doGet(request, response);

            verify(request, times(1)).getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp");
            verify(dispatcher, times(1)).forward(request, response);
        }
    }
}

// Commit: Development testing: Bổ sung các test case kiểm tra hiển thị thống kê biểu đồ Dashboard UC-12 | Author: Nguyễn Duy Khánh | Date: 2026-06-05 14:00:00
