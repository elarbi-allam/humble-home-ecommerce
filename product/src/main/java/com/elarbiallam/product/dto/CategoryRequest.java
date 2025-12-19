package com.elarbiallam.product.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
        @NotBlank(message = "Le nom de la catégorie est obligatoire")
        String name,

        String description
) {}