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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public void register(RegisterRequest request) {

        if (repository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();

        repository.save(user);
    }

    @Override
    public AuthResponse login(AuthRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
        } catch (Exception ex) {
            throw new InvalidCredentialsException();
        }

        User user = repository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException(request.email()));

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new AuthResponse(token);
    }
}