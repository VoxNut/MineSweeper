package com.minesweeper.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FirebaseUtil implements ServletContextListener {
    private static final Logger LOGGER = Logger.getLogger(FirebaseUtil.class.getName());
    private static FirebaseApp app;
    private static Firestore firestore;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        initialize();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (firestore != null) {
            try {
                firestore.close();
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, "Failed to close Firestore.", ex);
            }
        }
    }

    public static synchronized void initialize() {
        if (app != null) {
            return;
        }
        try (InputStream serviceAccount = FirebaseUtil.class.getClassLoader()
                .getResourceAsStream("serviceAccountKey.json")) {
            if (serviceAccount == null) {
                LOGGER.severe("serviceAccountKey.json not found in classpath.");
                return;
            }
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            List<FirebaseApp> apps = FirebaseApp.getApps();
            app = apps.isEmpty() ? FirebaseApp.initializeApp(options) : apps.get(0);
            firestore = FirestoreClient.getFirestore(app);
            LOGGER.info("Firebase initialized.");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Failed to initialize Firebase.", ex);
        }
    }

    public static Firestore getFirestore() {
        if (firestore == null) {
            initialize();
        }
        return firestore;
    }

    public static FirebaseAuth getAuth() {
        if (app == null) {
            initialize();
        }
        return FirebaseAuth.getInstance(app);
    }
}
