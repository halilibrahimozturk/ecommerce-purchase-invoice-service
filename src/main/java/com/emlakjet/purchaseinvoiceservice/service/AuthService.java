package com.emlakjet.purchaseinvoiceservice.service;

import com.emlakjet.purchaseinvoiceservice.dto.request.AuthRequest;
import com.emlakjet.purchaseinvoiceservice.dto.request.RegisterRequest;
import com.emlakjet.purchaseinvoiceservice.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(AuthRequest request);
    void register(RegisterRequest request);
}