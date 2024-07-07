package de.hsrm.quiz_gateway.firebase;

import java.io.IOException;
import java.io.InputStream;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.Resource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

@Configuration
public class FirebaseInitializer {

    @Autowired
    private ResourceLoader resourceLoader;

    @PostConstruct
    public void initialize() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:serviceAccountKey.json");
        InputStream serviceAccount = resource.getInputStream();

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);
    }

    @Bean
    public Firestore getDb() {
        return FirestoreClient.getFirestore();
    }
}
