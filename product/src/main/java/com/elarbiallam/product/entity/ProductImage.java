package com.elarbiallam.product.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl; // L'URL d'accès public

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}