package com.emlakjet.purchaseinvoiceservice.controller;

import com.emlakjet.purchaseinvoiceservice.dto.request.AuthRequest;
import com.emlakjet.purchaseinvoiceservice.dto.request.RegisterRequest;
import com.emlakjet.purchaseinvoiceservice.dto.response.ApiResponse;
import com.emlakjet.purchaseinvoiceservice.dto.response.AuthResponse;
import com.emlakjet.purchaseinvoiceservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentication",
        description = "Endpoints for user registration and authentication"
)
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Register a new purchasing specialist",
            description = """
                    Registers a new user with PURCHASING_SPECIALIST role.
                    Email must be unique in the system.
                    Password is stored in encrypted form.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User successfully registered"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Validation error or email already exists",
            content = @Content
    )
    @PostMapping("/register")
    public ApiResponse<Void> register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
        return ApiResponse.success("User registered successfully", null);
    }

    @Operation(
            summary = "User login",
            description = """
                    Authenticates user credentials and returns a JWT token.
                    The token must be used in Authorization header for secured endpoints.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(
                    schema = @Schema(implementation = AuthResponse.class)
            )
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Invalid email or password",
            content = @Content
    )
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
        return ApiResponse.success("Login successful", authService.login(request));
    }
}
