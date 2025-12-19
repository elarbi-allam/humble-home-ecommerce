package com.elarbiallam.user.dto;

import java.time.LocalDateTime;

public record UserResponse(
        String id, // keycloakId
        String firstname,
        String lastname,
        String email,
        String role,
        boolean active,
        LocalDateTime createdAt
) {}