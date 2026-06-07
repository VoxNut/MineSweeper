package com.minesweeper.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.minesweeper.model.GameConfig;
import com.minesweeper.util.FirebaseUtil;

public class GameConfigDAO {
    private final Firestore db;

    public GameConfigDAO() {
        this.db = FirebaseUtil.getFirestore();
    }

    public GameConfig getDefaultConfig() throws ExecutionException, InterruptedException {
        DocumentSnapshot snapshot = db.collection("gameConfig").document("default").get().get();
        if (snapshot.exists()) {
            GameConfig config = snapshot.toObject(GameConfig.class);
            return config != null ? config : GameConfig.defaultConfig();
        }
        return GameConfig.defaultConfig();
    }

    public void updateDefaultConfig(GameConfig config) throws ExecutionException, InterruptedException {
        updateConfig(config, config.getUpdatedBy());
    }

    public void ensureDefaultConfig() throws ExecutionException, InterruptedException {
        DocumentSnapshot snapshot = db.collection("gameConfig").document("default").get().get();
        if (!snapshot.exists()) {
            updateConfig(GameConfig.defaultConfig(), "system");
        }
    }

    public void updateConfig(GameConfig config, String adminUid) throws ExecutionException, InterruptedException {
        Map<String, Object> data = new HashMap<>();
        data.put("easy", config.getEasy());
        data.put("medium", config.getMedium());
        data.put("hard", config.getHard());
        data.put("updatedAt", config.getUpdatedAt());
        data.put("updatedBy", adminUid);
        db.collection("gameConfig").document("default").set(data, SetOptions.merge()).get();
    }
}
