package com.emlakjet.purchaseinvoiceservice.service;

import com.emlakjet.purchaseinvoiceservice.dto.request.AuthRequest;
import com.emlakjet.purchaseinvoiceservice.dto.request.RegisterRequest;
import com.emlakjet.purchaseinvoiceservice.dto.response.AuthResponse;
import com.emlakjet.purchaseinvoiceservice.exception.EmailAlreadyExistsException;
import com.emlakjet.purchaseinvoiceservice.exception.InvalidCredentialsException;

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