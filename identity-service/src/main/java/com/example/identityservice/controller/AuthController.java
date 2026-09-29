package com.example.identityservice.controller;

import com.example.identityservice.dto.LoginRequest;
import com.example.identityservice.dto.RegisterRequest;
import com.example.identityservice.dto.TokenResponse;
import com.example.identityservice.dto.UserResponse;
import com.example.identityservice.dto.RefreshTokenRequest;
import com.example.identityservice.repository.UserRepository;
import com.example.identityservice.service.AuthService;
import com.example.identityservice.service.UserService;
import com.example.identityservice.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.identityservice.service.BlacklistService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final BlacklistService blacklistService;

    public AuthController(
            UserService userService,
            UserRepository userRepository,
            JwtUtil jwtUtil,
            AuthService authService,
            BlacklistService blacklistService
    ) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authService = authService;
        this.blacklistService = blacklistService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @RequestBody RegisterRequest request
    ) {

        UserResponse response = userService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/test-token")
    public ResponseEntity<?> testToken(
            @RequestParam String username
    ) {

        return userRepository.findByUsername(username)
                .map(user ->
                        ResponseEntity.ok(
                                jwtUtil.generateAccessToken(user)
                        )
                )
                .orElseGet(() ->
                        ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body("User not found")
                );
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @RequestBody LoginRequest request
    ) {

        TokenResponse response =
                authService.login(request);

        return ResponseEntity.ok(response);
    }
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(
            @RequestBody RefreshTokenRequest request
    ) {

        TokenResponse response =
                authService.refreshToken(
                        request.getRefreshToken()
                );

        return ResponseEntity.ok(response);
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader("Authorization") String authorization
    ) {

        if (authorization == null ||
                !authorization.startsWith("Bearer ")) {

            return ResponseEntity
                    .badRequest()
                    .body("Missing Bearer token");
        }

        String token = authorization.substring(7);

        blacklistService.logout(token);

        return ResponseEntity.ok(
                "Logout successful"
        );
    }
}
