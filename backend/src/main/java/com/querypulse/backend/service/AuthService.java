package com.querypulse.backend.service;

import com.querypulse.backend.dto.ApiResponse;
import com.querypulse.backend.dto.LoginRequest;
import com.querypulse.backend.dto.LoginResponse;
import com.querypulse.backend.dto.RegisterRequest;

public interface AuthService {

    ApiResponse<Object> register(
            RegisterRequest request
    );

    ApiResponse<LoginResponse> login(
            LoginRequest request
    );
}