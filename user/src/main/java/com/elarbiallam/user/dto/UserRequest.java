package com.elarbiallam.user.dto;

// Un "record" (Java 17) est une classe immuable parfaite pour porter des données
public record UserRequest(
        String firstname,
        String lastname,
        String email,
        String role // "CLIENT" ou "ADMIN"
) {}