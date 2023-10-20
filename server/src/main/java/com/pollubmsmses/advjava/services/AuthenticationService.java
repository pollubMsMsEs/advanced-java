package com.pollubmsmses.advjava.services;

import com.pollubmsmses.advjava.controllers.*;
import com.pollubmsmses.advjava.models.User;
import com.pollubmsmses.advjava.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final  JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .message("Register successful")
                .token(jwtToken)
                .build();
    }
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
    public AuthenticationResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            var jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .message("Login successful")
                    .token(jwtToken)
                    .build();
        }
        return null;
    }

    public boolean doesUserExistByEmail(String email) {
        User existingUser = userRepository.findByEmail(email).orElse(null);
        return existingUser != null;
    }

    public boolean isPasswordCorrect(String email, String password) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            return passwordEncoder.matches(password, user.getPassword());
        }

        return false;
    }
}
