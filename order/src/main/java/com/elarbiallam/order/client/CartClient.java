package com.elarbiallam.order.client;

import com.elarbiallam.order.dto.CartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "cart") // Nom du service dans Eureka
public interface CartClient {

    // On ne passe pas d'ID, le token JWT sera propagé automatiquement si on configure un FeignInterceptor (on le verra plus tard)
    // Mais pour l'instant, faisons simple : le Cart Service attend le token dans le header.
    // L'implémentation basique ici suppose que le CartController utilise le JWT aussi.

    @GetMapping("/api/cart")
    CartResponse getMyCart();

    @DeleteMapping("/api/cart")
    void clearCart();
}