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
        // [UC-01] 1.1.6 / [UC-02] 2.1.7 / [UC-03] 3.1.8 / [UC-06] 6.1.7 Backend sử dụng Firebase Admin SDK để xác minh idToken.
        FirebaseToken token = FirebaseUtil.getAuth().verifyIdToken(idToken);
        // [UC-01] 1.1.7 / [UC-02] 2.1.8 / [UC-03] 3.1.8 / [UC-06] 6.1.8 Backend lấy các thông tin uid, email, name, photoURL từ token.
        String uid = token.getUid();
        String email = token.getEmail();
        String displayName = token.getName();
        String photoURL = token.getPicture();

        // [UC-01] 1.1.8 / [UC-02] 2.1.9 / [UC-03] 3.1.9 / [UC-06] 6.1.9 Backend truy vấn cơ sở dữ liệu (Firestore) để tìm bản ghi User dựa trên uid.
        User existing = userDAO.getByUid(uid);
        Timestamp now = Timestamp.now();
        User user = existing != null ? existing : new User();
        user.setUid(uid);
        user.setEmail(email);
        
        // [UC-01] 1.2.2.1 Cập nhật displayName và photoURL từ Google (áp dụng cho cả tạo mới và cập nhật).
        user.setDisplayName(displayName != null ? displayName : "");
        user.setPhotoURL(photoURL != null ? photoURL : "");
        
        if (existing == null) {
            // [UC-01] 1.1.9 / [UC-02] 2.2.2 / [UC-03] 3.1.10 Hệ thống tạo mới thông tin User (gán role="player", elo=1000, blocked=false).
            // [UC-02] 2.2.2.1 Đặt thuộc tính role là "player" / 2.2.2.2 Đặt elo mặc định.
            user.setRole("player");
            user.setBlocked(false);
            user.setEloRating(DEFAULT_ELO);
            user.setCreatedAt(now);
        } else {
            // [UC-01] 1.2.1 Backend nhận thấy uid đã tồn tại trong Firestore.
            // [UC-01] 1.2.2 Backend tiến hành cập nhật thông tin User thay vì tạo mới.
            if (user.getEloRating() <= 0) {
                user.setEloRating(DEFAULT_ELO);
            }
        }
        // [UC-01] 1.2.2.2 / [UC-02] 2.1.10 / [UC-06] 6.1.10 Cập nhật thời gian đăng nhập cuối cùng (lastLoginAt).
        user.setLastLoginAt(now);
        
        // [UC-01] 1.1.10 (hoặc 1.2.3) / [UC-02] 2.1.11 (hoặc 2.2.3) / [UC-03] 3.1.11 / [UC-06] 6.1.11 Backend lưu (upsert) đối tượng User.
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
