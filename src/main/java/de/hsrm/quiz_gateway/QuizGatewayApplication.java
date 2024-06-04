package de.hsrm.quiz_gateway;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.Resource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class QuizGatewayApplication {

	@Autowired
    private ResourceLoader resourceLoader;

	public static void main(String[] args) {
		SpringApplication.run(QuizGatewayApplication.class, args);
	}

	@PostConstruct
    public void init() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:serviceAccountKey.json");
        InputStream serviceAccount = resource.getInputStream();

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);
    }

}
