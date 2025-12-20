package com.elarbiallam.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product") // Le nom du service dans Eureka
public interface ProductClient {

    // Pour vérifier le prix/stock
    @GetMapping("/api/products/{id}")
    Object getProductById(@PathVariable("id") Long id);

    // Pour réduire le stock après commande (A implémenter plus tard dans ProductController)
    @PutMapping("/api/products/{id}/reduce-stock")
    void reduceStock(@PathVariable("id") Long id, @RequestParam int quantity);
}