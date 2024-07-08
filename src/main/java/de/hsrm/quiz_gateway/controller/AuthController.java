package de.hsrm.quiz_gateway.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Value;

import de.hsrm.quiz_gateway.firebase.firestore.services.UserService;
import de.hsrm.quiz_gateway.model.User;
import de.hsrm.quiz_gateway.request.ChangePasswordRequest;
import de.hsrm.quiz_gateway.request.GoogleLoginRequest;
import de.hsrm.quiz_gateway.request.LoginRequest;
import de.hsrm.quiz_gateway.request.SignupRequest;
import de.hsrm.quiz_gateway.service.UserDetailsServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.springframework.http.ResponseEntity.ok;



@RestController
@RequestMapping("/api")
//@CrossOrigin(origins = "http://34.95.109.147")
public class AuthController {

    // Google Client ID aus application.properties laden
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    private final UserDetailsServiceImpl userService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final UserService firestoreUserService;
    


    public AuthController(UserDetailsServiceImpl userService, UserDetailsService userDetailsService, AuthenticationManager authenticationManager, UserService firestoUserService) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.firestoreUserService = firestoUserService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest request) {
        User savedUser = userService.save(request.getUsername(),request.getPassword(),request.getEmail());
        try {
            firestoreUserService.createUser(savedUser.getUsername(), savedUser.getId() );
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return ok(savedUser);
    }
    

    @PostMapping("/loginNormal")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Authentication authRequest = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authResponse = this.authenticationManager.authenticate(authRequest);
        UserDetails user = (UserDetails) authResponse.getPrincipal();
        User userInformation = (User) authResponse.getPrincipal();
        String token = JWT.create().withSubject(user.getUsername()).withExpiresAt(Instant.now().plusSeconds(3600)).sign(Algorithm.HMAC256("1234"));
        Map<String, String> body = new HashMap<>();
        body.put("token",token);
        body.put("id", String.valueOf(userInformation.getId()));
        ResponseEntity<?> responseEntity = new ResponseEntity<>(body, HttpStatus.OK);
        return responseEntity;
    }


    // Login mit Google OAuth2
    @PostMapping("/google")
    public ResponseEntity<?> loginGoogle(@RequestBody GoogleLoginRequest googleLoginRequest) {
        try {
            // Verifiziere den ID Token mit Google
        System.out.println("Received Code: " + googleLoginRequest.getCode());
        System.out.println("Received ID Token: " + googleLoginRequest.getIdToken());
        
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(googleLoginRequest.getIdToken());

            if (idToken != null) {
                Payload payload = idToken.getPayload();

                // Extrahiere Benutzerinformationen aus dem Payload
                String userId = payload.getSubject();
                String email = payload.getEmail();
                String name = (String) payload.get("name");
                System.out.println("User ID: " + userId);
                System.out.println("User email: " + email);
                System.out.println("User name: " + name);

                // Erstelle JWT-Token für den Benutzer
                String token = JWT.create()
                        .withSubject(name)
                        .withExpiresAt(Instant.now().plusSeconds(3600))
                        .sign(Algorithm.HMAC256("1234"));

                // Erstelle die Antwort mit Token und Benutzer-ID
                Map<String, String> body = new HashMap<>();
                body.put("token", token);
                body.put("id", userId); // Beispiel für die Benutzer-ID, anpassen an deine Logik

                return ResponseEntity.ok(body);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //TODO
        return ok("password changed");
    }

    @GetMapping("/self")
    public ResponseEntity<?> getAuthenticatedUser() {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

}

