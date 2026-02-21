package com.library.library_management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the application.
 *
 * @Configuration — tells Spring "this class contains bean definitions, read it on startup."
 *
 * This class defines HOW requests are secured (or not) and registers
 * shared security utilities (like the password encoder) into Spring's container.
 *
 * Current state: WIDE OPEN — everything is permitted, no login required.
 * Future state (Day 8): Lock down endpoints by role (e.g., only ADMIN can delete books).
 */

@Configuration
public class SecurityConfig {

    /**
     * Defines the security filter chain — every HTTP request passes through
     * this chain BEFORE reaching your controllers.
     * <p>
     * Current rules:
     * 1. CSRF is DISABLED — CSRF attacks target browsers using cookies.
     * Since we'll use JWT tokens (stateless, no cookies), CSRF protection
     * is irrelevant and would just block our API calls.
     * <p>
     * 2. ALL requests are PERMITTED — no authentication required for any endpoint.
     * This is TEMPORARY for development. You'll replace .permitAll() with
     * role-based rules later (e.g., .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")).
     *
     * @param http Spring's HttpSecurity builder — fluent API to configure security rules
     * @return the built SecurityFilterChain that Spring applies to every request
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );
        return http.build();
    }

    /**
     * Registers BCryptPasswordEncoder as the app's password encoder.
     * <p>
     * WHY a @Bean? → So any class (e.g., UserService) can simply inject
     * PasswordEncoder via constructor and use it — no need to create new instances.
     * <p>
     * WHY BCrypt? → It's slow ON PURPOSE (makes brute-force attacks impractical),
     * adds a random salt per password (two identical passwords → different hashes),
     * and is the industry standard for password hashing.
     * <p>
     * Usage:
     * - Encoding:  passwordEncoder.encode("rawPassword")  → "$2a$10$xYz..."
     * - Matching:  passwordEncoder.matches("rawPassword", hashedPassword) → true/false
     *
     * @return a BCryptPasswordEncoder instance managed by Spring's container
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
