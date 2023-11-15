package com.pollubmsmses.advjava.controllers;

import com.pollubmsmses.advjava.models.auth.AuthenticationRequest;
import com.pollubmsmses.advjava.models.auth.AuthenticationResponse;
import com.pollubmsmses.advjava.models.auth.LoginRequest;
import com.pollubmsmses.advjava.models.auth.RegisterRequest;
import com.pollubmsmses.advjava.services.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid
            @RequestBody RegisterRequest request
    ){

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
            @Valid
            @RequestBody LoginRequest request
    ) {

        return ResponseEntity.ok(service.login(request));
    }
}

