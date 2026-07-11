package com.IssueWatch.API.controllers;

import com.IssueWatch.API.dto.request.LoginRequest;
import com.IssueWatch.API.dto.request.LogoutRequest;
import com.IssueWatch.API.dto.request.RefreshRequest;
import com.IssueWatch.API.dto.request.RegisterRequest;
import com.IssueWatch.API.dto.response.AuthResponse;
import com.IssueWatch.API.dto.response.MessageResponse;
import com.IssueWatch.API.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        MessageResponse response = authService.register(request);

        return ResponseEntity
                .status(201)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);

        return ResponseEntity
                .ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        AuthResponse response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@Valid @RequestBody LogoutRequest request) {
        MessageResponse response = authService.logout(request);

        return ResponseEntity.ok(response);
    }
}
