package com.ozturk.purchaseinvoiceservice.service;

import com.ozturk.purchaseinvoiceservice.dto.request.AuthRequest;
import com.ozturk.purchaseinvoiceservice.dto.request.RegisterRequest;
import com.ozturk.purchaseinvoiceservice.dto.response.AuthResponse;
import com.ozturk.purchaseinvoiceservice.exception.EmailAlreadyExistsException;
import com.ozturk.purchaseinvoiceservice.exception.InvalidCredentialsException;

/**
 * Service for user authentication and registration.
 *
 * <p>Handles user login and registration operations.
 */
public interface AuthService {

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param request login credentials (email and password)
     * @return authentication response containing JWT token
     * @throws InvalidCredentialsException if login fails
     */
    AuthResponse login(AuthRequest request);

    /**
     * Registers a new user in the system.
     *
     * @param request user registration details
     * @throws EmailAlreadyExistsException if email is already registered
     */
    void register(RegisterRequest request);
}