package com.example.identityservice.service;

import com.example.identityservice.dto.LoginRequest;
import com.example.identityservice.dto.TokenResponse;
import com.example.identityservice.entity.RefreshToken;
import com.example.identityservice.entity.User;
import com.example.identityservice.repository.RefreshTokenRepository;
import com.example.identityservice.repository.UserRepository;
import com.example.identityservice.util.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.identityservice.exception.InvalidRefreshTokenException;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            RefreshTokenService refreshTokenService,
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public TokenResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(
                request.getUsername()
        ).orElseThrow(() ->
                new RuntimeException("Invalid username or password")
        );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new RuntimeException(
                    "Invalid username or password"
            );
        }

        String accessToken =
                jwtUtil.generateAccessToken(user);

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(
                        user.getId()
                );

        return new TokenResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer"
        );
    }

    @Transactional
    public TokenResponse refreshToken(String requestToken) {

        // 1. Find the refresh token
        RefreshToken refreshToken =
                refreshTokenRepository.findByToken(requestToken)
                        .orElseThrow(() ->
                                new InvalidRefreshTokenException(
                                        "Invalid refresh token"
                                )
                        );

        // 2. Check expiration
        refreshTokenService.verifyExpiration(refreshToken);

        // 3. Get the user BEFORE deleting the old token
        User user = refreshToken.getUser();

        // 4. Delete old refresh token
        refreshTokenRepository.delete(refreshToken);

        // 5. Create a new access token
        String accessToken =
                jwtUtil.generateAccessToken(user);

        // 6. Create a completely new refresh token
        RefreshToken newRefreshToken =
                refreshTokenService.createRefreshToken(
                        user.getId()
                );

        // 7. Return the new token pair
        return new TokenResponse(
                accessToken,
                newRefreshToken.getToken(),
                "Bearer"
        );
    }
}