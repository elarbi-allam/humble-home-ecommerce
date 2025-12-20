package com.elarbiallam.order.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private Long productId;

    // On sauvegarde le nom au moment de la commande (au cas où il change plus tard)
    private String productName;

    private int quantity;

    private BigDecimal price; // Prix au moment de l'achat
}