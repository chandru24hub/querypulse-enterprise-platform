package com.querypulse.backend.service.impl;

import com.querypulse.backend.dto.ApiResponse;
import com.querypulse.backend.dto.RegisterRequest;
import com.querypulse.backend.entity.User;
import com.querypulse.backend.exception.ResourceAlreadyExistsException;
import com.querypulse.backend.repository.UserRepository;
import com.querypulse.backend.service.AuthService;

import com.querypulse.backend.dto.LoginRequest;
import com.querypulse.backend.dto.LoginResponse;
import com.querypulse.backend.exception.InvalidCredentialsException;
import com.querypulse.backend.security.JwtService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    @Override
    public ApiResponse<Object> register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {

            throw new ResourceAlreadyExistsException(
                    "Email already exists"
            );
        }

        if (userRepository.existsByUsername(request.getUsername())) {

            throw new ResourceAlreadyExistsException(
                    "Username already exists"
            );
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )
                .build();

        userRepository.save(user);

        return ApiResponse.builder()
                .success(true)
                .message("User registered successfully")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
public ApiResponse<LoginResponse> login(
        LoginRequest request
) {

    User user = userRepository.findByEmail(
                    request.getEmail()
            )
            .orElseThrow(() ->
                    new InvalidCredentialsException(
                            "Invalid email or password"
                    )
            );

    boolean passwordMatches =
            passwordEncoder.matches(
                    request.getPassword(),
                    user.getPasswordHash()
            );

    if (!passwordMatches) {

        throw new InvalidCredentialsException(
                "Invalid email or password"
        );
    }

    String token =
            jwtService.generateToken(
        user.getEmail(),
        user.getRole().name()
);

    LoginResponse loginResponse =
            LoginResponse.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .build();

    return ApiResponse.<LoginResponse>builder()
            .success(true)
            .message("Login successful")
            .data(loginResponse)
            .timestamp(LocalDateTime.now())
            .build();
}
}