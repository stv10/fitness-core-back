package com.fitness.backend.service;

import com.fitness.backend.dto.AuthDtos.*;
import com.fitness.backend.model.Role;
import com.fitness.backend.model.User;
import com.fitness.backend.repository.UserRepository;
import com.fitness.backend.security.JwtUtility;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtility jwtUtility;

    public UserService(UserRepository userRepository, JwtUtility jwtUtility) {
        this.userRepository = userRepository;
        this.jwtUtility = jwtUtility;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already registered");
        }

        String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
        
        // Default role is USER for standard registrations
        User user = new User(
                request.getEmail(),
                hashedPassword,
                request.getFirstName(),
                request.getLastName(),
                Role.USER
        );

        User savedUser = userRepository.save(user);
        
        String token = jwtUtility.generateToken(savedUser.getId(), savedUser.getEmail(), savedUser.getRole());
        UserResponse userResponse = new UserResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getRole()
        );

        return new AuthResponse(token, userResponse);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        String token = jwtUtility.generateToken(user.getId(), user.getEmail(), user.getRole());
        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole()
        );

        return new AuthResponse(token, userResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole()
        );
    }
}
