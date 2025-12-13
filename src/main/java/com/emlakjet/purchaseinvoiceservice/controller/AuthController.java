package com.emlakjet.purchaseinvoiceservice.controller;

import com.emlakjet.purchaseinvoiceservice.dto.response.ApiResponse;
import com.emlakjet.purchaseinvoiceservice.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ApiResponse<String> login(@RequestParam String username,
                                     @RequestParam String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        String token = jwtUtil.generateToken(authentication.getName());

        return ApiResponse.success("Login successful", token);
    }

}