package com.pollubmsmses.advjava.services;

import com.pollubmsmses.advjava.exceptions.UserAlreadyExistsException;
import com.pollubmsmses.advjava.models.Role;
import com.pollubmsmses.advjava.models.User;
import com.pollubmsmses.advjava.models.auth.AuthenticationResponse;
import com.pollubmsmses.advjava.models.auth.LoginRequest;
import com.pollubmsmses.advjava.models.auth.RegisterRequest;
import com.pollubmsmses.advjava.repositories.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@RunWith(MockitoJUnitRunner.class)
public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    public void testRegister() {
        RegisterRequest request = new RegisterRequest("Test", "test@example.com", "password123", Role.USER);

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);

        when(passwordEncoder.encode(request.getPassword())).thenReturn("password123");

        when(jwtService.generateToken(any())).thenReturn("JwtToken");

        AuthenticationResponse response = authenticationService.register(request);

        assertNotNull(response);
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals(request.getName(), response.getName());
        assertEquals(String.valueOf(request.getRole()), response.getRole());

        verify(userRepository, times(1)).save(any());

        verify(passwordEncoder, times(1)).encode(request.getPassword());

        verify(jwtService, times(1)).generateToken(any());
    }

    @Test
    public void testLogin() {
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        User user = User.builder()
                .name("Test")
                .email("test@example.com")
                .password(passwordEncoder.encode("password123"))
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(java.util.Optional.of(user));

        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);

        when(jwtService.generateToken(user)).thenReturn("JwtToken");

        AuthenticationResponse response = authenticationService.login(request);

        assertNotNull(response);
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals(user.getName(), response.getName());
        assertEquals(String.valueOf(user.getRole()), response.getRole());

        verify(jwtService, times(1)).generateToken(user);
    }

    @Test
    public void testRegisterUserAlreadyExists() {
        RegisterRequest request = new RegisterRequest("Test", "test@example.com", "password123", Role.USER);

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authenticationService.register(request));

        verify(userRepository, never()).save(any());

        verify(passwordEncoder, never()).encode(any());

        verify(jwtService, never()).generateToken(any());
    }
}

