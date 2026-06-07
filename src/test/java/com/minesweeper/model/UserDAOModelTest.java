package com.minesweeper.model;

import com.minesweeper.model.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserDAOModelTest {

    @Test
    public void testUserModelGettersAndSetters() {
        User user = new User();
        user.setUid("firebase_user_123");
        user.setDisplayName("testuser");
        user.setEmail("test@gmail.com");
        user.setRole("user");
        user.setBlocked(true);
        user.setEloRating(1200);
        
        assertEquals("firebase_user_123", user.getUid());
        assertEquals("testuser", user.getDisplayName());
        assertEquals("test@gmail.com", user.getEmail());
        assertEquals("user", user.getRole());
        assertTrue(user.isBlocked());
        assertEquals(1200, user.getEloRating());
    }
}
