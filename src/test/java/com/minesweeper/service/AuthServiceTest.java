/**
 * release testing cho chức năng đăng nhập bằng google
 * người thực hiện test : Nguyễn Đức Khải
 * người làm chức năng: Võ Minh Nhựt
 */
package com.minesweeper.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.minesweeper.dao.UserDAO;
import com.minesweeper.model.User;
import com.minesweeper.util.FirebaseUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

public class AuthServiceTest {

    @Test
    public void testVerifyAndUpsert_newUser() throws Exception {
        String fakeIdToken = "fake-token";
        String uid = "uid-new";
        String email = "new@example.com";
        String name = "New User";
        String pic = "http://pic";

        FirebaseAuth mockAuth = mock(FirebaseAuth.class);
        FirebaseToken mockToken = mock(FirebaseToken.class);
        when(mockToken.getUid()).thenReturn(uid);
        when(mockToken.getEmail()).thenReturn(email);
        when(mockToken.getName()).thenReturn(name);
        when(mockToken.getPicture()).thenReturn(pic);
        when(mockAuth.verifyIdToken(fakeIdToken)).thenReturn(mockToken);

        UserDAO mockUserDao = mock(UserDAO.class);
        when(mockUserDao.getByUid(uid)).thenReturn(null);

        try (MockedStatic<FirebaseUtil> mocked = mockStatic(FirebaseUtil.class)) {
            mocked.when(FirebaseUtil::getAuth).thenReturn(mockAuth);

            AuthService svc = new AuthService();

            Field daoField = AuthService.class.getDeclaredField("userDAO");
            daoField.setAccessible(true);
            daoField.set(svc, mockUserDao);

            AuthService.AuthResult res = svc.verifyAndUpsert(fakeIdToken);

            assertNotNull(res);
            assertEquals(uid, res.getUid());
            assertEquals(name, res.getDisplayName());
            assertEquals("player", res.getRole());
            assertEquals(pic, res.getPhotoURL());
            assertFalse(res.isBlocked());

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(mockUserDao, times(1)).upsert(captor.capture());
            User saved = captor.getValue();
            assertEquals(uid, saved.getUid());
            assertEquals(email, saved.getEmail());
            assertEquals(name, saved.getDisplayName());
            assertEquals(pic, saved.getPhotoURL());
            assertEquals("player", saved.getRole());
        }
    }

    @Test
    public void testVerifyAndUpsert_existingUser_keepsElo() throws Exception {
        String fakeIdToken = "fake-token-2";
        String uid = "uid-exist";
        String email = "exist@example.com";
        String name = "Exist User";
        String pic = "http://pic2";

        FirebaseAuth mockAuth = mock(FirebaseAuth.class);
        FirebaseToken mockToken = mock(FirebaseToken.class);
        when(mockToken.getUid()).thenReturn(uid);
        when(mockToken.getEmail()).thenReturn(email);
        when(mockToken.getName()).thenReturn(name);
        when(mockToken.getPicture()).thenReturn(pic);
        when(mockAuth.verifyIdToken(fakeIdToken)).thenReturn(mockToken);

        User existing = new User();
        existing.setUid(uid);
        existing.setEmail(email);
        existing.setDisplayName("Old Name");
        existing.setEloRating(1500);

        UserDAO mockUserDao = mock(UserDAO.class);
        when(mockUserDao.getByUid(uid)).thenReturn(existing);

        try (MockedStatic<FirebaseUtil> mocked = mockStatic(FirebaseUtil.class)) {
            mocked.when(FirebaseUtil::getAuth).thenReturn(mockAuth);

            AuthService svc = new AuthService();
            Field daoField = AuthService.class.getDeclaredField("userDAO");
            daoField.setAccessible(true);
            daoField.set(svc, mockUserDao);

            AuthService.AuthResult res = svc.verifyAndUpsert(fakeIdToken);

            assertNotNull(res);
            assertEquals(uid, res.getUid());
            assertEquals(name, res.getDisplayName());
            assertEquals(1500, existing.getEloRating());

            // upsert should have been called to update lastLogin and displayName/photo
            verify(mockUserDao, times(1)).upsert(any(User.class));
        }
    }
}
