package com.emlakjet.purchaseinvoiceservice.service.impl;

import com.emlakjet.purchaseinvoiceservice.dto.request.AuthRequest;
import com.emlakjet.purchaseinvoiceservice.dto.request.RegisterRequest;
import com.emlakjet.purchaseinvoiceservice.dto.response.AuthResponse;
import com.emlakjet.purchaseinvoiceservice.model.UserRole;
import com.emlakjet.purchaseinvoiceservice.model.entity.User;
import com.emlakjet.purchaseinvoiceservice.repository.UserRepository;
import com.emlakjet.purchaseinvoiceservice.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;

    private RegisterRequest registerRequest;
    private AuthRequest authRequest;
    private User user;

    @BeforeEach
    void setup() {

        registerRequest = new RegisterRequest(
                "Halil Ibrahim",
                "Ozturk",
                "halilibrahim@ozturk.com",
                "password",
                UserRole.PURCHASING_SPECIALIST
        );

        authRequest = new AuthRequest(
                "halilibrahim@ozturk.com",
                "password"
        );

        user = User.builder()
                .firstName("Halil Ibrahim")
                .lastName("Ozturk")
                .email("halilibrahim@ozturk.com")
                .password("encoded-password")
                .role(UserRole.PURCHASING_SPECIALIST)
                .build();
    }

    @Test
    void register_shouldSaveUserSuccessfully() {

        when(userRepository.existsByEmail("halilibrahim@ozturk.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded-password");

        authService.register(registerRequest);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User saved = captor.getValue();
        assertEquals("halilibrahim@ozturk.com", saved.getEmail());
        assertEquals("encoded-password", saved.getPassword());
    }

    @Test
    void register_shouldThrow_whenEmailExists() {

        when(userRepository.existsByEmail("halilibrahim@ozturk.com")).thenReturn(true);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> authService.register(registerRequest)
        );

        assertEquals("Email already exists", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_shouldReturnJwtToken() {

        Authentication authentication =
                new UsernamePasswordAuthenticationToken("halilibrahim@ozturk.com", null);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(userRepository.findByEmail("halilibrahim@ozturk.com"))
                .thenReturn(Optional.of(user));

        when(jwtUtil.generateToken("halilibrahim@ozturk.com", UserRole.PURCHASING_SPECIALIST.name()))
                .thenReturn("jwt-token");

        AuthResponse response = authService.login(authRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.token());
    }

    @Test
    void login_shouldThrow_whenAuthenticationFails() {

        when(authenticationManager.authenticate(any()))
                .thenThrow(new RuntimeException("Bad credentials"));

        assertThrows(RuntimeException.class,
                () -> authService.login(authRequest));

        verify(jwtUtil, never()).generateToken(any(), any());
    }

    @Test
    void login_shouldThrow_whenUserNotFound() {

        Authentication authentication =
                new UsernamePasswordAuthenticationToken("halilibrahim@ozturk.com", null);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(userRepository.findByEmail("halilibrahim@ozturk.com"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.login(authRequest));

        assertEquals("User not found", ex.getMessage());
    }


}
