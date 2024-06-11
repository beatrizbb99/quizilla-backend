package de.hsrm.quiz_gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsrm.quiz_gateway.model.User;
import de.hsrm.quiz_gateway.service.AuthService;

import java.io.IOException;
import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;
    private String responseBody;

    @PostMapping("/login")
    public String loginUser(@RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");
            String password = payload.get("password");
            responseBody = authService.loginUser(email, password);
            saveUser(responseBody);
            return responseBody;

        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody Map<String, String> payload) {
        try {
            String name = payload.get("name");
            String email = payload.get("email");
            String password = payload.get("password");
            responseBody = authService.registerUser(name, email, password);
            saveUser(responseBody);
            return responseBody;

        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    public void saveUser(String responseBody) throws IOException {
        Map<String, Object> responseMap = null;
    
        try {
            if (responseBody != null) {
                ObjectMapper mapper = new ObjectMapper();
                responseMap = mapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // Logge die Fehlermeldung oder gib eine benutzerfreundliche Fehlermeldung aus
            throw new IOException("Error processing JSON response");
        }
    
        if (responseMap != null) {
            Map<String, Object> userData = (Map<String, Object>) responseMap.get("user");
            
            // Benutzer-Objekt erstellen und Daten setzen
            User user = new User();
            user.setId((Integer) userData.get("id"));
            user.setName((String) userData.get("name"));
            user.setEmail((String) userData.get("email"));
            user.setPassword((String) userData.get("password"));
            user.setCreatedAt((String) userData.get("created_at"));
           
            System.out.println(user);
        } else {
            throw new IOException("Empty response received from server");
        }
    }
}

