package com.quevedo.mercado.inventario.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * Utilidad para generar, firmar y validar tokens JWT (algoritmo HS256).
 * Los roles del usuario (por ejemplo "ROLE_ADMIN") se incluyen como claim
 * "roles" dentro del token, de forma que el filtro de autenticacion pueda
 * reconstruir las authorities de Spring Security sin volver a consultar
 * ninguna fuente de usuarios.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Getter
    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username, List<String> roles) {
        Date now = new Date();
        Date expiracion = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiracion)
                .signWith(signingKey())
                .compact();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractClaims(token).get("roles", List.class);
    }

    /**
     * Valida la firma y la expiracion del token. Cualquier problema
     * (firma invalida, formato incorrecto, token expirado) se traduce
     * a "false" en vez de propagar la excepcion, dejando que el filtro
     * continue la cadena como una peticion no autenticada (401 si el
     * endpoint lo requiere).
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

}
