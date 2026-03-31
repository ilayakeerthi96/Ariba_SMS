package com.itti.gateway.service;

import java.util.Date;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String extractUserName(String token);

    String generateToken(UserDetails userDetails, Date expirationTime);

    boolean isTokenValid(String token, UserDetails userDetails);
}
