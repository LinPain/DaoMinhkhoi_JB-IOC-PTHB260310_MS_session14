package com.example.identityservice.dto;

public class TokenResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;

    public TokenResponse() {
    }

    public TokenResponse(
            String accessToken,
            String refreshToken,
            String tokenType
    ) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }
}