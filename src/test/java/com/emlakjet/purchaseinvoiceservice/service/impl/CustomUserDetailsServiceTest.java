package com.emlakjet.purchaseinvoiceservice.service.impl;

import com.emlakjet.purchaseinvoiceservice.model.UserRole;
import com.emlakjet.purchaseinvoiceservice.model.entity.User;
import com.emlakjet.purchaseinvoiceservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setup() {

        user = User.builder()
                .email("halilibrahim@ozturk.com")
                .password("encoded-password")
                .role(UserRole.PURCHASING_SPECIALIST)
                .build();
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {

        when(userRepository.findByEmail("halilibrahim@ozturk.com"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails =
                userDetailsService.loadUserByUsername("halilibrahim@ozturk.com");

        assertNotNull(userDetails);
        assertEquals("halilibrahim@ozturk.com", userDetails.getUsername());
        assertEquals("encoded-password", userDetails.getPassword());
    }

    @Test
    void loadUserByUsername_shouldMapRoleWithROLEPrefix() {

        when(userRepository.findByEmail("halilibrahim@ozturk.com"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails =
                userDetailsService.loadUserByUsername("halilibrahim@ozturk.com");

        assertEquals(1, userDetails.getAuthorities().size());

        GrantedAuthority authority =
                userDetails.getAuthorities().iterator().next();

        assertEquals("ROLE_PURCHASING_SPECIALIST", authority.getAuthority());
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserNotFound() {

        when(userRepository.findByEmail("halilibrahim@ozturk.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("halilibrahim@ozturk.com")
        );
    }

    @Test
    void loadUserByUsername_shouldCallRepositoryOnce() {

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(user));

        userDetailsService.loadUserByUsername("halilibrahim@ozturk.com");

        verify(userRepository, times(1))
                .findByEmail("halilibrahim@ozturk.com");
    }

}
