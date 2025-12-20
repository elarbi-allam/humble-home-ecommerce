package com.elarbiallam.cart.controller;

import com.elarbiallam.cart.dto.AddToCartRequest;
import com.elarbiallam.cart.dto.CartResponse;
import com.elarbiallam.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Récupérer mon panier
    @GetMapping
    public ResponseEntity<CartResponse> getMyCart(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject(); // Récupère le "sub" (ID unique) du token Keycloak
        return ResponseEntity.ok(cartService.getMyCart(userId));
    }

    // Ajouter un item
    @PostMapping("/items")
    public ResponseEntity<Void> addItem(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid AddToCartRequest request) {
        String userId = jwt.getSubject();
        cartService.addItemToCart(userId, request);
        return ResponseEntity.ok().build();
    }

    // Supprimer un item
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeItem(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productId) {
        String userId = jwt.getSubject();
        cartService.removeItem(userId, productId);
        return ResponseEntity.ok().build();
    }

    // Vider le panier
    @DeleteMapping
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        cartService.clearCart(userId);
        return ResponseEntity.ok().build();
    }
}