package de.hsrm.quiz_gateway.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class GoogleCloudStorageConfig {

    @Bean
    public Storage storage() throws IOException {
        String credentialsPath = "/storageKey.json";
        try (InputStream credentialsStream = getClass().getResourceAsStream(credentialsPath)) {
            if (credentialsStream == null) {
                throw new IOException("Service Account Key File not found: " + credentialsPath);
            }
            return StorageOptions.newBuilder()
                    .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                    .build()
                    .getService();
        }
    }
}