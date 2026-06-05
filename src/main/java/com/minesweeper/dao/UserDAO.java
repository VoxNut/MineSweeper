package com.minesweeper.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.minesweeper.model.User;
import com.minesweeper.util.FirebaseUtil;

public class UserDAO {
    private final Firestore db;

    public UserDAO() {
        this.db = FirebaseUtil.getFirestore();
    }

    @SuppressWarnings("null")
    public User getByUid(String uid) throws ExecutionException, InterruptedException {
        DocumentSnapshot snapshot = db.collection("users").document(uid).get().get();
        return snapshot.exists() ? snapshot.toObject(User.class) : null;
    }

    public User getUserByUid(String uid) throws ExecutionException, InterruptedException {
        return getByUid(uid);
    }

    @SuppressWarnings("null")
    public void upsert(User user) throws ExecutionException, InterruptedException {
        db.collection("users").document(user.getUid()).set(user).get();
    }

    @SuppressWarnings("null")
    public void updateBlockStatus(String uid, boolean isBlocked) throws ExecutionException, InterruptedException {
        db.collection("users").document(uid).update("isBlocked", isBlocked).get();
    }

    @SuppressWarnings("null")
    public void updateUserField(String uid, String field, Object value) throws ExecutionException, InterruptedException {
        db.collection("users").document(uid).update(field, value).get();
    }

    @SuppressWarnings("null")
    public void updateEloRating(String uid, int eloRating) throws ExecutionException, InterruptedException {
        db.collection("users").document(uid).update("eloRating", eloRating).get();
    }

    public List<User> getAll() throws ExecutionException, InterruptedException {
        QuerySnapshot snapshot = db.collection("users").get().get();
        List<User> users = new ArrayList<>();
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            User user = doc.toObject(User.class);
            if (user != null) {
                users.add(user);
            }
        }
        return users;
    }

    public List<User> getAllUsers() throws ExecutionException, InterruptedException {
        return getAll();
    }
}
