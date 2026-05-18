
package com.itti.leadcapturing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Security Configuration
 * Location: src/main/java/com/itti/leadcapturing/config/SecurityConfig.java
 * 
 * UPDATE THIS FILE - Add hierarchy auth endpoints to permitAll
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ============================================
    // CORS Configuration
    // ============================================
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:4200",
            "http://localhost:3000",
            "http://localhost:8080",
            "http://127.0.0.1:4200",
            "http://127.0.0.1:8080",
            "http://117.239.138.177:4200",
            "http://117.239.138.177:8080"
        ));
        
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
        ));
        
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Auth-Token",
            "Access-Control-Allow-Origin"
        ));
        
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }

    // ============================================
    // Security Filter Chain
    // ============================================
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Admin auth endpoints
                .requestMatchers("/api/admin/auth/**").permitAll()
                
                // Buyer auth endpoints
                .requestMatchers("/api/buyer/auth/**").permitAll()
                .requestMatchers("/api/buyer/login").permitAll()
                
                // 🆕 Hierarchy auth endpoints (Procurement, COO, Finance)
                .requestMatchers("/api/hierarchy/auth/**").permitAll()
                
                // Supplier auth endpoints
                .requestMatchers("/api/supplier/auth/**").permitAll()
                
                // Health check endpoints
                .requestMatchers("/api/*/health").permitAll()

                // WebSocket endpoints for real-time chat (SockJS uses HTTP handshake)
                .requestMatchers("/ws/**", "/ws/chat/**").permitAll()

                // Allow all other requests (you can restrict later)
                .anyRequest().permitAll()
            );
        
        return http.build();
    }
}