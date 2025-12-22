package com.elarbiallam.order.controller;

import com.elarbiallam.order.dto.OrderRequest;
import com.elarbiallam.order.dto.OrderResponse;
import com.elarbiallam.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Long> createOrder(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid OrderRequest request) {
        String userId = jwt.getSubject();
        Long orderId = orderService.createOrder(userId, request);
        return ResponseEntity.ok(orderId);
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponse>> getMyOrders(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(orderService.getMyOrders(userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    // --- ENDPOINT ADMIN ---

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')") // Sécurité Keycloak
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    @PutMapping("/{orderId}/pay")
    public ResponseEntity<Void> updateOrderStatusToPaid(@PathVariable Long orderId) {
        orderService.payOrder(orderId);
        return ResponseEntity.ok().build();
    }
}