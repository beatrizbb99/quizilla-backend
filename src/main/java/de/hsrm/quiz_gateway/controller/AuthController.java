package de.hsrm.quiz_gateway.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import de.hsrm.quiz_gateway.model.User;
import de.hsrm.quiz_gateway.request.ChangePasswordRequest;
import de.hsrm.quiz_gateway.request.LoginRequest;
import de.hsrm.quiz_gateway.request.SignupRequest;
import de.hsrm.quiz_gateway.response.TokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;


@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthenticationManager authenticationManager;


    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Authentication authRequest = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authResponse = this.authenticationManager.authenticate(authRequest);
        UserDetails user = (UserDetails) authResponse.getPrincipal();
        String token = JWT.create().withSubject(user.getUsername()).withExpiresAt(Instant.now().plusSeconds(3600)).sign(Algorithm.HMAC256("1234"));
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(token);
        return ResponseEntity.ok(tokenResponse);
    }

    @GetMapping("/self")
    public ResponseEntity<?> getAuthenticatedUser() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
