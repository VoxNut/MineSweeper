package com.minesweeper.util;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;

public class AdminSeeder {
    private static final Logger LOGGER = Logger.getLogger(AdminSeeder.class.getName());

    @SuppressWarnings("null")
    public static void main(String[] args) throws Exception {
        // Init Firebase
        FileInputStream serviceAccount =
            new FileInputStream("src/main/resources/serviceAccountKey.json");
        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build();
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }

        // Create Firebase Auth user
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
            .setEmail("msadmin@minesweeper.local")
            .setPassword("msadmin123321@1")
            .setDisplayName("msadmin")
            .setEmailVerified(true);

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        String uid = userRecord.getUid();
        LOGGER.log(Level.INFO, "Admin UID: {0}", uid);

        // Write to Firestore users collection
        Firestore db = FirestoreClient.getFirestore();
        Map<String, Object> adminData = new HashMap<>();
        adminData.put("uid", uid);
        adminData.put("email", "msadmin@minesweeper.local");
        adminData.put("displayName", "msadmin");
        adminData.put("photoURL", "");
        adminData.put("role", "admin");
        adminData.put("isBlocked", false);
        adminData.put("createdAt", Timestamp.now());
        adminData.put("lastLoginAt", Timestamp.now());

        db.collection("users").document(uid).set(adminData).get();
        LOGGER.log(Level.INFO, "Admin user created in Firestore. UID: {0}", uid);
        System.exit(0);
    }
}
