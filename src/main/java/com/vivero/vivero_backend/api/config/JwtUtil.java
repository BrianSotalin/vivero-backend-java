package com.vivero.vivero_backend.api.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import com.vivero.vivero_backend.api.model.Usuario;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expirationTime;

    private Key getKey() {
        byte[] keyBytes = Base64.getDecoder().decode(
            Base64.getEncoder().encodeToString(secret.getBytes())
        );
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateTokenJWT(Usuario usuario) {
        return Jwts.builder()
                .setSubject(usuario.getUsername())
                .claim("rol", usuario.getRol())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getKey())
                .compact();
    }

    public String extractRol(String token) {
        return (String) extractAllClaims(token).get("rol");
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}