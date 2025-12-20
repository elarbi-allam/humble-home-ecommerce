package com.elarbiallam.order.dto;

public record UserResponse(
        String id,
        String email,
        String firstName,
        String lastName
) {}