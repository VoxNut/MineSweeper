package com.minesweeper.service;

import com.google.cloud.Timestamp;
import com.google.firebase.auth.FirebaseToken;
import com.minesweeper.dao.UserDAO;
import com.minesweeper.model.User;
import com.minesweeper.util.FirebaseUtil;

public class AuthService {
    private static final int DEFAULT_ELO = 1000;
    private final UserDAO userDAO = new UserDAO();

    public AuthResult verifyAndUpsert(String idToken) throws Exception {
        FirebaseToken token = FirebaseUtil.getAuth().verifyIdToken(idToken);
        String uid = token.getUid();
        String email = token.getEmail();
        String displayName = token.getName();
        String photoURL = token.getPicture();

        User existing = userDAO.getByUid(uid);
        Timestamp now = Timestamp.now();
        User user = existing != null ? existing : new User();
        user.setUid(uid);
        user.setEmail(email);
        user.setDisplayName(displayName != null ? displayName : "");
        user.setPhotoURL(photoURL != null ? photoURL : "");
        if (existing == null) {
            user.setRole("player");
            user.setBlocked(false);
            user.setEloRating(DEFAULT_ELO);
            user.setCreatedAt(now);
        } else if (user.getEloRating() <= 0) {
            user.setEloRating(DEFAULT_ELO);
        }
        user.setLastLoginAt(now);
        userDAO.upsert(user);

        return new AuthResult(user.getUid(), user.getDisplayName(), user.getRole(), user.getPhotoURL(), user.isBlocked());
    }

    public static class AuthResult {
        private final String uid;
        private final String displayName;
        private final String role;
        private final String photoURL;
        private final boolean blocked;

        public AuthResult(String uid, String displayName, String role, String photoURL, boolean blocked) {
            this.uid = uid;
            this.displayName = displayName;
            this.role = role;
            this.photoURL = photoURL;
            this.blocked = blocked;
        }

        public String getUid() {
            return uid;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getRole() {
            return role;
        }

        public String getPhotoURL() {
            return photoURL;
        }

        public boolean isBlocked() {
            return blocked;
        }
    }
}
