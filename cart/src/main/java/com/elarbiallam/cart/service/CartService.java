package com.elarbiallam.cart.service;

import com.elarbiallam.cart.client.ProductClient;
import com.elarbiallam.cart.dto.*;
import com.elarbiallam.cart.entity.Cart;
import com.elarbiallam.cart.entity.CartItem;
import com.elarbiallam.cart.exception.BusinessException;
import com.elarbiallam.cart.exception.ResourceNotFoundException;
import com.elarbiallam.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final ProductClient productClient;

    @Transactional
    public CartResponse getMyCart(String userId) {
        Cart cart = getOrCreateCart(userId);
        return mapToResponse(cart);
    }

    @Transactional
    public void addItemToCart(String userId, AddToCartRequest request) {
        Cart cart = getOrCreateCart(userId);

        // 1. Vérifier le produit via Feign
        ProductResponse product;
        try {
            product = productClient.getProductById(request.productId());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Produit introuvable (ID: " + request.productId() + ")");
        }

        // 2. Vérifier le stock
        if (product.inventory() < request.quantity()) {
            throw new BusinessException("Stock insuffisant pour le produit : " + product.name());
        }

        // 3. Vérifier si l'item existe déjà
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.productId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.quantity());
            item.calculateTotal(); // Recalcule le prix ligne
        } else {
            String imageUrl = (product.imageUrls() != null && !product.imageUrls().isEmpty())
                    ? product.imageUrls().get(0)
                    : null;

            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productId(product.id())
                    .productName(product.name())
                    .productImageUrl(imageUrl)
                    .quantity(request.quantity())
                    .unitPrice(product.price())
                    .build();
            newItem.calculateTotal();
            cart.getItems().add(newItem);
        }

        cart.recalculateTotal();
        cartRepository.save(cart);
    }

    @Transactional
    public void removeItem(String userId, Long productId) {
        Cart cart = getOrCreateCart(userId);

        boolean removed = cart.getItems().removeIf(item -> item.getProductId().equals(productId));

        if (!removed) {
            throw new ResourceNotFoundException("Produit non trouvé dans le panier");
        }

        cart.recalculateTotal();
        cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(String userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().clear();
        cart.recalculateTotal();
        cartRepository.save(cart);
    }

    // --- Helpers ---

    private Cart getOrCreateCart(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return cartRepository.save(newCart);
                });
    }

    private CartResponse mapToResponse(Cart cart) {
        return new CartResponse(
                cart.getId(),
                cart.getUserId(),
                cart.getTotalAmount(),
                cart.getItems().stream().map(this::mapItemToResponse).collect(Collectors.toSet())
        );
    }

    private CartItemResponse mapItemToResponse(CartItem item) {
        return new CartItemResponse(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getProductImageUrl(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotalPrice()
        );
    }
}