package com.emlakjet.purchaseinvoiceservice.controller;

import com.emlakjet.purchaseinvoiceservice.dto.request.AuthRequest;
import com.emlakjet.purchaseinvoiceservice.dto.request.RegisterRequest;
import com.emlakjet.purchaseinvoiceservice.dto.response.AuthResponse;
import com.emlakjet.purchaseinvoiceservice.dto.response.CommonApiResponse;
import com.emlakjet.purchaseinvoiceservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
            summary = "Register new user",
            description = """
                    Registers a new user in the system with one of the following roles:
                                        
                    - PURCHASING_SPECIALIST
                    - FINANCE_SPECIALIST
                                        
                    Rules:
                    - Email must be unique
                    - Password is stored in encrypted form
                    - Role must be one of the supported roles
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "User successfully registered",
            content = @Content(
                    schema = @Schema(implementation = CommonApiResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Validation error or invalid role",
            content = @Content(
                    schema = @Schema(implementation = CommonApiResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "409",
            description = "Email already exists",
            content = @Content(
                    schema = @Schema(implementation = CommonApiResponse.class)
            )
    )
    @PostMapping("/register")
    public CommonApiResponse<Void> register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
        return CommonApiResponse.success("User registered successfully", null);
    }

    @Operation(
            summary = "User login",
            description = """
                    Authenticates user credentials and returns a JWT token.
                                        
                    JWT Token:
                    - Contains user's email as subject
                    - Contains user's role (PURCHASING_SPECIALIST or FINANCE_SPECIALIST)
                                        
                    Usage:
                    Authorization: Bearer <JWT_TOKEN>
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(
                    schema = @Schema(implementation = AuthResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Validation error (invalid request body)",
            content = @Content(
                    schema = @Schema(implementation = CommonApiResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "401",
            description = "Invalid email or password",
            content = @Content(
                    schema = @Schema(implementation = CommonApiResponse.class)
            )
    )
    @PostMapping("/login")
    public CommonApiResponse<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
        return CommonApiResponse.success("Login successful", authService.login(request));
    }
}
