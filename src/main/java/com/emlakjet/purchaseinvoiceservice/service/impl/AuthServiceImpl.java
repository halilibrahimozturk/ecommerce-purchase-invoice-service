package com.emlakjet.purchaseinvoiceservice.service.impl;

import com.emlakjet.purchaseinvoiceservice.dto.request.AuthRequest;
import com.emlakjet.purchaseinvoiceservice.dto.request.RegisterRequest;
import com.emlakjet.purchaseinvoiceservice.dto.response.AuthResponse;
import com.emlakjet.purchaseinvoiceservice.model.UserRole;
import com.emlakjet.purchaseinvoiceservice.model.entity.PurchasingSpecialist;
import com.emlakjet.purchaseinvoiceservice.repository.PurchasingSpecialistRepository;
import com.emlakjet.purchaseinvoiceservice.service.AuthService;
import com.emlakjet.purchaseinvoiceservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PurchasingSpecialistRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public void register(RegisterRequest request) {

        if (repository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }

        PurchasingSpecialist user = PurchasingSpecialist.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(UserRole.PURCHASING_SPECIALIST)
                .build();

        repository.save(user);
    }

    @Override
    public AuthResponse login(AuthRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        String token = jwtUtil.generateToken(authentication.getName());

        return new AuthResponse(token);
    }
}