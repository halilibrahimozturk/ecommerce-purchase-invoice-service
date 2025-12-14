package com.emlakjet.purchaseinvoiceservice.service.impl;

import com.emlakjet.purchaseinvoiceservice.dto.request.AuthRequest;
import com.emlakjet.purchaseinvoiceservice.dto.request.RegisterRequest;
import com.emlakjet.purchaseinvoiceservice.dto.response.AuthResponse;
import com.emlakjet.purchaseinvoiceservice.exception.EmailAlreadyExistsException;
import com.emlakjet.purchaseinvoiceservice.exception.InvalidCredentialsException;
import com.emlakjet.purchaseinvoiceservice.exception.UserNotFoundException;
import com.emlakjet.purchaseinvoiceservice.model.entity.User;
import com.emlakjet.purchaseinvoiceservice.repository.UserRepository;
import com.emlakjet.purchaseinvoiceservice.service.AuthService;
import com.emlakjet.purchaseinvoiceservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Business service responsible for authentication and user registration.
 *
 * Responsibilities:
 * - Register new users
 * - Authenticate users with Spring Security
 * - Generate JWT tokens after successful login
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    /**
     * Registers a new user in the system.
     *
     * @param request registration request containing user details
     * @throws EmailAlreadyExistsException if email is already registered
     */
    @Override
    public void register(RegisterRequest request) {

        log.info("Register attempt for email={}", request.email());

        if (repository.existsByEmail(request.email())) {
            log.warn("Registration failed - email already exists: {}", request.email());
            throw new EmailAlreadyExistsException(request.email());
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                // Password is encoded before saving for security reasons
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();

        repository.save(user);
        log.info("User successfully registered with email={}", request.email());
    }

    /**
     * Authenticates user credentials and returns JWT token.
     *
     * @param request login request containing email and password
     * @return AuthResponse containing generated JWT token
     * @throws InvalidCredentialsException if authentication fails
     */
    @Override
    public AuthResponse login(AuthRequest request) {

        log.info("Login attempt for email={}", request.email());

        try {
            // Delegates authentication to Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
        } catch (Exception ex) {
            log.warn("Login failed due to invalid credentials for email={}", request.email());
            throw new InvalidCredentialsException();
        }

        User user = repository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException(request.email()));

        // JWT contains email and role for authorization checks
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        log.info("Login successful for email={}", request.email());
        return new AuthResponse(token);
    }
}
