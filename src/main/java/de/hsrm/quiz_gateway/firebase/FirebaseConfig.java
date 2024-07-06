package de.hsrm.quiz_gateway.firebase;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;

@Configuration
public class FirebaseConfig {

    @Bean
    public Firestore getDb() {
        return FirestoreClient.getFirestore();
    }
    
}
