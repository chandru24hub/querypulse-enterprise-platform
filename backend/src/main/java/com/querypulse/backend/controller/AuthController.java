package com.querypulse.backend.controller;

import com.querypulse.backend.dto.ApiResponse;
import com.querypulse.backend.dto.LoginRequest;
import com.querypulse.backend.dto.LoginResponse;
import com.querypulse.backend.dto.RegisterRequest;
import com.querypulse.backend.service.AuthService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<Object> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        return authService.register(request);
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        return authService.login(request);
    }
}