package com.elarbiallam.product.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "Le nom est obligatoire")
        String name,

        String brand,

        @NotNull @Positive(message = "Le prix doit être positif")
        BigDecimal price,

        @Min(value = 0, message = "Le stock ne peut pas être négatif")
        int inventory,

        String description,

        @NotNull(message = "L'ID de catégorie est obligatoire")
        Long categoryId
) {}