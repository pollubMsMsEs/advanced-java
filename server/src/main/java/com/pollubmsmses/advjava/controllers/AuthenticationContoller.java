package com.pollubmsmses.advjava.controllers;

import com.pollubmsmses.advjava.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthenticationContoller {
    private final AuthenticationService service;
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ){
        String email = request.getEmail();
        String password = request.getPassword();
        String username = request.getName();

        if (username.length() < 3) {
            String errorMessage = "The username must be at least 3 characters long";
            AuthenticationResponse errorResponse = AuthenticationResponse.builder()
                    .message(errorMessage)
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }

        if (password == null || password.length() < 6) {
            String errorMessage = "The password must be at least 6 characters long";
            AuthenticationResponse errorResponse = AuthenticationResponse.builder()
                    .message(errorMessage)
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }


        if (!isValidEmail(email) || email.isEmpty()) {
            String errorMessage = "Invalid email format";
            AuthenticationResponse errorResponse = AuthenticationResponse.builder()
                    .message(errorMessage)
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }

        if (service.doesUserExistByEmail(email)) {
            String errorMessage = "A user with the provided email address already exists";
            AuthenticationResponse errorResponse = AuthenticationResponse.builder()
                    .message(errorMessage)
                    .build();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ){
        return ResponseEntity.ok(service.authenticate(request));
    }
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody LoginRequest request
    ) {
        String email = request.getEmail();
        String password = request.getPassword();

        if (!service.doesUserExistByEmail(email)) {
            String errorMessage = "A user with the provided email address does not exist";
            AuthenticationResponse errorResponse = AuthenticationResponse.builder()
                    .message(errorMessage)
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        if (!service.isPasswordCorrect(email, password)) {
            String errorMessage = "Invalid password";
            AuthenticationResponse errorResponse = AuthenticationResponse.builder()
                    .message(errorMessage)
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        return ResponseEntity.ok(service.login(request));
    }
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
