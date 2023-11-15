package com.pollubmsmses.advjava;
import com.pollubmsmses.advjava.models.Role;
import com.pollubmsmses.advjava.models.auth.LoginRequest;
import com.pollubmsmses.advjava.models.auth.RegisterRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;


import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class ValidationTest {

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    @Test
    public void testValidRegisterRequest() {
        RegisterRequest validRequest = RegisterRequest.builder()
                .name("Test")
                .email("test@example.com")
                .password("password123")
                .role(Role.USER)
                .build();

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(validRequest);

        assertEquals(0, violations.size(), "There should be no validation errors for a valid request");
    }

    @Test
    public void testInvalidName() {
        RegisterRequest invalidRequest = RegisterRequest.builder()
                .name("A")
                .email("test@example.com")
                .password("password123")
                .role(Role.USER)
                .build();

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(invalidRequest);

        assertEquals(1, violations.size(), "There should be 1 validation error for the invalid name");
        assertEquals("The name must have a length between 3 and 50 characters.", violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidEmail() {
        RegisterRequest invalidRequest = RegisterRequest.builder()
                .name("test")
                .email("invalidEmail")
                .password("password123")
                .role(Role.USER)
                .build();

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(invalidRequest);

        assertEquals(1, violations.size(), "There should be 1 validation error for the invalid email");
        assertEquals("The email address is invalid.", violations.iterator().next().getMessage());
    }

    @Test
    public void testBlankPassword() {
        RegisterRequest invalidRequest = RegisterRequest.builder()
                .name("test")
                .email("test@example.com")
                .password(" ")
                .role(Role.USER)
                .build();

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(invalidRequest);

        assertEquals(1, violations.size(), "There should be 1 validation error for the blank password");
        assertEquals("Password is mandatory", violations.iterator().next().getMessage());
    }


    @Test
    public void testValidationWithBlankEmailForLogin() {
        LoginRequest invalidRequest = LoginRequest.builder()
                .email(" ")
                .password("password123")
                .build();

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(invalidRequest);

        assertEquals(1, violations.size(), "There should be 1 validation error for the blank email");

        ConstraintViolation<LoginRequest> violation = violations.iterator().next();
        assertEquals("Email is mandatory", violation.getMessage());
        assertEquals("email", violation.getPropertyPath().toString());
    }

    @Test
    public void testValidationWithBlankPasswordForLogin()  {
        LoginRequest invalidRequest = LoginRequest.builder()
                .email("test@example.com")
                .password(" ")
                .build();

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(invalidRequest);

        assertEquals(1, violations.size(), "There should be 1 validation error for the blank password");

        ConstraintViolation<LoginRequest> violation = violations.iterator().next();
        assertEquals("Password is mandatory", violation.getMessage());
        assertEquals("password", violation.getPropertyPath().toString());
    }

    @Test
    public void testValidLoginRequest() {
        LoginRequest validRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(validRequest);

        assertEquals(0, violations.size(), "There should be no validation errors for a valid login request");
    }
}