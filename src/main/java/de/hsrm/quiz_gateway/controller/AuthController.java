package de.hsrm.quiz_gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;



@RestController
public class AuthController {

    private final UserDetailsServiceImpl userService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;


    public AuthController(UserDetailsServiceImpl userService, UserDetailsService userDetailsService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest request) {
        User savedUser = userService.save(request.getUsername(),request.getPassword(),request.getEmail());
        return ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Authentication authRequest = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authResponse = this.authenticationManager.authenticate(authRequest);
        UserDetails user = (UserDetails) authResponse.getPrincipal();
        String token = JWT.create().withSubject(user.getUsername()).withExpiresAt(Instant.now().plusSeconds(3600)).sign(Algorithm.HMAC256("1234"));
        ResponseEntity responseEntity = new ResponseEntity<>(token, HttpStatus.OK);
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

