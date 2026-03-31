package com.itti.leadcapturing.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class JwtService {
    
    @Value("${jwt.secret:your_super_secret_key_12345}")
    private String jwtSecret;
    
    @Value("${jwt.expiration:86400000}")  // 24 hours in milliseconds
    private long jwtExpiration;
    
    /**
     * Generate JWT token for authenticated user
     */
    public String generateToken(Long userId, String email, Long departmentId, String role) {
        return Jwts.builder()
                .setSubject(userId.toString())                    // Subject: User ID
                .claim("email", email)                            // Claim: Email
                .claim("departmentId", departmentId)              // Claim: Department ID
                .claim("role", role)                              // Claim: Role
                .setIssuedAt(new Date())                          // Issued at: Now
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Expiry: 24 hours
                .signWith(SignatureAlgorithm.HS512, jwtSecret)    // Sign with HS512
                .compact();
    }
}