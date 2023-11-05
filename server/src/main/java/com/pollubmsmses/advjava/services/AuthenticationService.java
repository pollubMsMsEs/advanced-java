package com.pollubmsmses.advjava.services;

import com.pollubmsmses.advjava.exceptions.InvalidPasswordException;
import com.pollubmsmses.advjava.exceptions.UserAlreadyExistsException;
import com.pollubmsmses.advjava.exceptions.UserNotFoundException;
import com.pollubmsmses.advjava.models.User;
import com.pollubmsmses.advjava.models.auth.AuthenticationRequest;
import com.pollubmsmses.advjava.models.auth.AuthenticationResponse;
import com.pollubmsmses.advjava.models.auth.LoginRequest;
import com.pollubmsmses.advjava.models.auth.RegisterRequest;
import com.pollubmsmses.advjava.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final  JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with this email already exists");
        }
        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .name(user.getName())
                .role(String.valueOf(user.getRole()))
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
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isEmpty()) {
            throw new UserNotFoundException("User with this email does not exist");
        }


        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            var jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .email(user.getEmail())
                    .name(user.getName())
                    .role(String.valueOf(user.getRole()))
                    .build();
        }
        else {
            throw new InvalidPasswordException("Invalid password");
        }
    }
}
