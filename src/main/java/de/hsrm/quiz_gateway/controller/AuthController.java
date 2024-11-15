package de.hsrm.quiz_gateway.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import de.hsrm.quiz_gateway.model.User;
import de.hsrm.quiz_gateway.request.ChangePasswordRequest;
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
import org.springframework.web.bind.annotation.*;
import de.hsrm.quiz_gateway.firebase.firestore.services.UserService;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.springframework.http.ResponseEntity.ok;




@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://34.149.22.243")
public class AuthController {

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

