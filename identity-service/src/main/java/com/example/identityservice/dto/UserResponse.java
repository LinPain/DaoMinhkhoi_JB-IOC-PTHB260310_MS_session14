package com.example.identityservice.dto;

import java.util.UUID;

public class UserResponse {

    private UUID id;
    private String username;
    private String role;

    public UserResponse(UUID id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}