package com.elarbiallam.user.dto;

public record UserUpdateRequest(
        String firstname,
        String lastname
) {}