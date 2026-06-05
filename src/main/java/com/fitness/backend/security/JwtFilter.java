package com.fitness.backend.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fitness.backend.model.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtility jwtUtility;

    public JwtFilter(JwtUtility jwtUtility) {
        this.jwtUtility = jwtUtility;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Bypass security checks for public endpoints
        if (path.equals("/api/auth/login") || 
            path.equals("/api/auth/register") || 
            path.equals("/api/ping") || 
            path.startsWith("/actuator") ||
            request.getMethod().equalsIgnoreCase("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorizedError(response, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7);

        try {
            DecodedJWT decodedJWT = jwtUtility.verifyToken(token);
            UUID userId = jwtUtility.getUserIdFromToken(decodedJWT);
            String email = jwtUtility.getEmailFromToken(decodedJWT);
            Role role = jwtUtility.getRoleFromToken(decodedJWT);

            // Attach user claims to request attributes
            request.setAttribute("authenticatedUserId", userId);
            request.setAttribute("authenticatedUserEmail", email);
            request.setAttribute("authenticatedUserRole", role);

            filterChain.doFilter(request, response);
        } catch (JWTVerificationException e) {
            sendUnauthorizedError(response, "Invalid token: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            sendUnauthorizedError(response, "Invalid UUID format in token");
        }
    }

    private void sendUnauthorizedError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format("{\"error\":\"Unauthorized\",\"message\":\"%s\"}", message));
    }
}
