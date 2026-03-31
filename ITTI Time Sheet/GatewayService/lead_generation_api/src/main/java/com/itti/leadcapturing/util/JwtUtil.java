// // package com.itti.leadcapturing.util;

// // import io.jsonwebtoken.Claims;
// // import io.jsonwebtoken.Jwts;
// // import io.jsonwebtoken.SignatureAlgorithm;
// // import io.jsonwebtoken.security.Keys;
// // import org.springframework.beans.factory.annotation.Value;
// // import org.springframework.stereotype.Component;

// // import javax.crypto.SecretKey;
// // import java.nio.charset.StandardCharsets;
// // import java.util.Date;
// // import java.util.HashMap;
// // import java.util.Map;
// // import java.util.function.Function;

// // @Component
// // public class JwtUtil {

// //     @Value("${jwt.secret}")
// //     private String secret;

// //     @Value("${jwt.expiration}")
// //     private Long expiration;

// //     private SecretKey getSigningKey() {
// //         byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
// //         return Keys.hmacShaKeyFor(keyBytes);
// //     }

// //     public String generateToken(String username, String role) {
// //         Map<String, Object> claims = new HashMap<>();
// //         claims.put("role", role);
// //         return createToken(claims, username);
// //     }

// //     private String createToken(Map<String, Object> claims, String subject) {
// //         Date now = new Date();
// //         Date expiryDate = new Date(now.getTime() + expiration);

// //         return Jwts.builder()
// //                 .setClaims(claims)
// //                 .setSubject(subject)
// //                 .setIssuedAt(now)
// //                 .setExpiration(expiryDate)
// //                 .signWith(getSigningKey(), SignatureAlgorithm.HS512)
// //                 .compact();
// //     }

// //     public String extractUsername(String token) {
// //         return extractClaim(token, Claims::getSubject);
// //     }

// //     public String extractRole(String token) {
// //         return extractClaim(token, claims -> claims.get("role", String.class));
// //     }

// //     public Date extractExpiration(String token) {
// //         return extractClaim(token, Claims::getExpiration);
// //     }

// //     public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
// //         final Claims claims = extractAllClaims(token);
// //         return claimsResolver.apply(claims);
// //     }

// //     private Claims extractAllClaims(String token) {
// //         return Jwts.parserBuilder()
// //                 .setSigningKey(getSigningKey())
// //                 .build()
// //                 .parseClaimsJws(token)
// //                 .getBody();
// //     }

// //     public Boolean isTokenExpired(String token) {
// //         return extractExpiration(token).before(new Date());
// //     }

// //     public Boolean validateToken(String token, String username) {
// //         final String extractedUsername = extractUsername(token);
// //         return (extractedUsername.equals(username) && !isTokenExpired(token));
// //     }

// //     public Long getExpirationTime() {
// //         return System.currentTimeMillis() + expiration;
// //     }
// // }

// // ==================== FILE: src/main/java/com/itti/leadcapturing/util/JwtUtil.java ====================

// package com.itti.leadcapturing.util;

// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;
// import io.jsonwebtoken.security.Keys;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Component;

// import javax.crypto.SecretKey;
// import java.nio.charset.StandardCharsets;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.function.Function;

// /**
//  * ✅ FIXED: JWT Utility with User ID support
//  */
// @Component
// public class JwtUtil {

//     @Value("${jwt.secret}")
//     private String secret;

//     @Value("${jwt.expiration}")
//     private Long expiration;

//     // ============================================
//     // ✅ ORIGINAL METHOD (Backward compatibility)
//     // ============================================
//     public String generateToken(String email, String role) {
//         return generateTokenWithId(email, role, null);
//     }

//     // ============================================
//     // ✅ NEW METHOD: Generate token WITH user ID
//     // ============================================
//     public String generateTokenWithId(String email, String role, Long userId) {
//         Map<String, Object> claims = new HashMap<>();
//         claims.put("role", role);
        
//         if (userId != null) {
//             claims.put("userId", userId.toString()); // ✅ CRITICAL: Store user ID in token
//         }
        
//         return createToken(claims, email);
//     }

//     // ============================================
//     // Create JWT Token
//     // ============================================
//     private String createToken(Map<String, Object> claims, String subject) {
//         Date now = new Date();
//         Date expirationDate = new Date(now.getTime() + expiration);

//         return Jwts.builder()
//                 .setClaims(claims)
//                 .setSubject(subject)
//                 .setIssuedAt(now)
//                 .setExpiration(expirationDate)
//                 .signWith(getSigningKey(), SignatureAlgorithm.HS512)
//                 .compact();
//     }

//     // ============================================
//     // Extract Claims
//     // ============================================
//     public Claims extractAllClaims(String token) {
//         return Jwts.parserBuilder()
//                 .setSigningKey(getSigningKey())
//                 .build()
//                 .parseClaimsJws(token)
//                 .getBody();
//     }

//     public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//         final Claims claims = extractAllClaims(token);
//         return claimsResolver.apply(claims);
//     }

//     // ============================================
//     // Extract User Information
//     // ============================================
//     public String extractEmail(String token) {
//         return extractClaim(token, Claims::getSubject);
//     }

//     public String extractRole(String token) {
//         return extractClaim(token, claims -> claims.get("role", String.class));
//     }

//     /**
//      * ✅ NEW: Extract user ID from token
//      */
//     public Long extractUserId(String token) {
//         String userIdStr = extractClaim(token, claims -> claims.get("userId", String.class));
//         return userIdStr != null ? Long.parseLong(userIdStr) : null;
//     }

//     public Date extractExpiration(String token) {
//         return extractClaim(token, Claims::getExpiration);
//     }

//     // ============================================
//     // Token Validation
//     // ============================================
//     private Boolean isTokenExpired(String token) {
//         return extractExpiration(token).before(new Date());
//     }

//     public Boolean validateToken(String token, String email) {
//         final String extractedEmail = extractEmail(token);
//         return (extractedEmail.equals(email) && !isTokenExpired(token));
//     }

//     public Boolean validateToken(String token) {
//         try {
//             return !isTokenExpired(token);
//         } catch (Exception e) {
//             return false;
//         }
//     }

//     // ============================================
//     // Get Signing Key
//     // ============================================
//     private SecretKey getSigningKey() {
//         byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
//         return Keys.hmacShaKeyFor(keyBytes);
//     }

//     // ============================================
//     // ✅ UTILITY: Print Token Info (for debugging)
//     // ============================================
//     public void printTokenInfo(String token) {
//         try {
//             Claims claims = extractAllClaims(token);
//             System.out.println("=".repeat(60));
//             System.out.println("TOKEN INFO:");
//             System.out.println("  Email: " + claims.getSubject());
//             System.out.println("  Role: " + claims.get("role"));
//             System.out.println("  User ID: " + claims.get("userId"));
//             System.out.println("  Issued At: " + claims.getIssuedAt());
//             System.out.println("  Expires At: " + claims.getExpiration());
//             System.out.println("=".repeat(60));
//         } catch (Exception e) {
//             System.out.println("Error parsing token: " + e.getMessage());
//         }
//     }
// }

package com.itti.leadcapturing.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * ✅ Generate token WITH user ID (for all user types)
     */
    public String generateTokenWithId(String email, String role, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("role", role);
        claims.put("userId", userId);  // ✅ CRITICAL: Include user ID
        
        return createToken(claims, email);
    }

    /**
     * Legacy method for backward compatibility
     */
    public String generateToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("role", role);
        
        return createToken(claims, email);
    }

    /**
     * Create JWT token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extract email from token
     */
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Extract role from token
     */
    public String extractRole(String token) {
        return (String) extractClaims(token).get("role");
    }

    /**
     * ✅ Extract user ID from token
     */
    public Long extractUserId(String token) {
        Object userId = extractClaims(token).get("userId");
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        } else if (userId instanceof Long) {
            return (Long) userId;
        }
        return null;
    }

    /**
     * Extract all claims from token
     */
    private Claims extractClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Validate token
     */
    public Boolean validateToken(String token, String email) {
        final String extractedEmail = extractEmail(token);
        return (extractedEmail.equals(email) && !isTokenExpired(token));
    }

    /**
     * Check if token is expired
     */
    private Boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}