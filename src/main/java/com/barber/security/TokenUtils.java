package com.barber.security;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.barber.entities.Rol;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class TokenUtils {

    private static final String SECRET_KEY = "586E3272357538782F413F4428472B4B6250655368566B597033733676397924"; // Debería ser una clave más segura en producción
    private final static Long ACCESS_TOKEN_VALIDITY_SECONDS = 2_592_00L; // 30 días en segundos

    public static String createToken(String nombre, String email, Rol rolId) {
        long expirationTime = ACCESS_TOKEN_VALIDITY_SECONDS * 1_000; // Convertir a milisegundos
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);

        Map<String, Object> claims = new HashMap<>();
        claims.put("nombre", nombre);
        claims.put("rolId", rolId);

        return Jwts.builder()
            .setSubject(email)
            .setExpiration(expirationDate)
            .addClaims(claims)
            .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
            .compact();
    }

    public static UsernamePasswordAuthenticationToken getAuthentication(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();

            String email = claims.getSubject();

            if (email != null) {
                // Aquí puedes agregar roles u otras autoridades si es necesario
                return new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
            }
            return null;
        } catch (JwtException | IllegalArgumentException e) {
            // Manejo de errores adecuado (por ejemplo, logging)
            return null;
        }
    }
}
