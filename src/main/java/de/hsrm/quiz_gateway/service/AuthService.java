package de.hsrm.quiz_gateway.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.IdTokenCredentials;
import com.google.auth.oauth2.IdTokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private static final String CLOUD_FUNCTION_URL = "https://europe-west1-flowing-gasket-421115.cloudfunctions.net/";
    private static final String LOGIN_FUNCTION = "function-1";
    private static final String REGISTER_FUNCTION = "function-2";

    // Methode zum Einloggen eines Benutzers
    public String loginUser(String email, String password) throws IOException {
        Map<String, String> requestBody = createRequestBodyLogin(email, password);
        return callCloudFunction(LOGIN_FUNCTION, requestBody);
    }

    // Methode zum Registrieren eines Benutzers
    public String registerUser(String name, String email, String password) throws IOException {
        Map<String, String> requestBody = createRequestBodyRegister(name, email, password);
        return callCloudFunction(REGISTER_FUNCTION, requestBody);
    }

    private String callCloudFunction(String functionName, Map<String, String> requestBody) throws IOException {
        GoogleCredentials credentials;
        try (FileInputStream serviceAccountStream = new FileInputStream("src\\main\\resources\\CloudFunctionKey.json")) {
            credentials = GoogleCredentials.fromStream(serviceAccountStream);
        }
        if (!(credentials instanceof IdTokenProvider)) {
            throw new IllegalArgumentException("Credentials are not an instance of IdTokenProvider.");
        }
        IdTokenCredentials tokenCredential = IdTokenCredentials.newBuilder()
                .setIdTokenProvider((IdTokenProvider) credentials)
                .setTargetAudience(CLOUD_FUNCTION_URL + functionName)
                .build();

        // Forcefully refresh the token
        tokenCredential.refresh();
        String idToken = tokenCredential.getAccessToken().getTokenValue();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + idToken);
        headers.set("Content-Type", "application/json");

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                CLOUD_FUNCTION_URL + functionName, HttpMethod.POST, entity, String.class);

        return response.getBody();
    }

    private Map<String, String> createRequestBodyRegister(String name, String email, String password) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", name);
        requestBody.put("email", email);
        requestBody.put("password", password);
        return requestBody;
    }
    private Map<String, String> createRequestBodyLogin(String email, String password) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("password", password);
        return requestBody;
    }
}
