package com.elarbiallam.cart.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // L'ID de l'utilisateur (lié au User Service via Keycloak ID ou ID numérique)
    // Ici on garde String car Keycloak utilise des UUID (ex: "123e4567-e89b...")
    // Si ton User Service utilise des Int, change en Long/Integer.
    // Pour être flexible et compatible avec le token, je recommande String (le "sub" du token).
    @Column(unique = true, nullable = false)
    private String userId;

    private BigDecimal totalAmount = BigDecimal.ZERO;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> items = new HashSet<>();

    public void recalculateTotal() {
        this.totalAmount = items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}