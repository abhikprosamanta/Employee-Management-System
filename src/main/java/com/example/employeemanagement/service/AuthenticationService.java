package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.auth.AuthResponse;
import com.example.employeemanagement.dto.auth.LoginRequest;
import com.example.employeemanagement.dto.auth.RegisterRequest;

public interface AuthenticationService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
