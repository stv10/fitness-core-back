package com.fitness.backend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fitness.backend.model.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtility {

    private final Algorithm algorithm;
    private final String issuer = "fitness-core";
    private final long expirationMs;

    public JwtUtility(
            @Value("${app.jwt.secret:default-very-secure-secret-key-1234567890-fitness-core}") String secret,
            @Value("${app.jwt.expiration-ms:86400000}") long expirationMs // default 24 hours
    ) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.expirationMs = expirationMs;
    }

    public String generateToken(UUID userId, String email, Role role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return JWT.create()
                .withIssuer(issuer)
                .withSubject(userId.toString())
                .withClaim("email", email)
                .withClaim("role", role.name())
                .withIssuedAt(now)
                .withExpiresAt(expiryDate)
                .sign(algorithm);
    }

    public DecodedJWT verifyToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
        return verifier.verify(token);
    }

    public UUID getUserIdFromToken(DecodedJWT jwt) {
        return UUID.fromString(jwt.getSubject());
    }

    public String getEmailFromToken(DecodedJWT jwt) {
        return jwt.getClaim("email").asString();
    }

    public Role getRoleFromToken(DecodedJWT jwt) {
        String roleStr = jwt.getClaim("role").asString();
        return Role.valueOf(roleStr);
    }
}
