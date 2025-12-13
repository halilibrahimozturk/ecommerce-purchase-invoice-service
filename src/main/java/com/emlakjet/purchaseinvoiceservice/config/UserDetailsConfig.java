package com.emlakjet.purchaseinvoiceservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class UserDetailsConfig {

    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
        return new InMemoryUserDetailsManager(
                User.withUsername("purchaser1")
                        .password(passwordEncoder.encode("purchaser1"))
                        .roles("USER")
                        .build(),
                User.withUsername("purchaser2")
                        .password(passwordEncoder.encode("purchaser2"))
                        .roles("USER")
                        .build()
        );
    }
}