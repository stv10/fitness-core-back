package com.fitness.backend.controller;

import com.fitness.backend.dto.AuthDtos.*;
import com.fitness.backend.model.Role;
import com.fitness.backend.security.JwtUtility;
import com.fitness.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtility jwtUtility;

    @Test
    public void testRegisterSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        UserResponse userResponse = new UserResponse(userId, "test@fitness.com", "Test", "User", Role.USER);
        AuthResponse authResponse = new AuthResponse("mock-jwt-token", userResponse);

        Mockito.when(userService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        String jsonPayload = """
                {
                    "email": "test@fitness.com",
                    "password": "securepassword",
                    "firstName": "Test",
                    "lastName": "User"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("mock-jwt-token")))
                .andExpect(jsonPath("$.user.email", is("test@fitness.com")))
                .andExpect(jsonPath("$.user.firstName", is("Test")));
    }

    @Test
    public void testLoginSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        UserResponse userResponse = new UserResponse(userId, "test@fitness.com", "Test", "User", Role.USER);
        AuthResponse authResponse = new AuthResponse("mock-jwt-token", userResponse);

        Mockito.when(userService.login(any(LoginRequest.class))).thenReturn(authResponse);

        String jsonPayload = """
                {
                    "email": "test@fitness.com",
                    "password": "securepassword"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("mock-jwt-token")))
                .andExpect(jsonPath("$.user.email", is("test@fitness.com")));
    }

    @Test
    public void testMeEndpointUnauthorized() throws Exception {
        // Hitting /api/auth/me without headers should return 401 Unauthorized via JwtFilter
        mockMvc.perform(get("/api/auth/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
