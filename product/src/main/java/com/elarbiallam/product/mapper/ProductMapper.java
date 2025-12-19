package com.elarbiallam.product.mapper;

import com.elarbiallam.product.dto.ProductRequest;
import com.elarbiallam.product.dto.ProductResponse;
import com.elarbiallam.product.entity.Category;
import com.elarbiallam.product.entity.Product;
import com.elarbiallam.product.entity.ProductImage;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class ProductMapper {

    public Product toEntity(ProductRequest request, Category category) {
        return Product.builder()
                .name(request.name())
                .brand(request.brand())
                .price(request.price())
                .inventory(request.inventory())
                .description(request.description())
                .category(category)
                .build();
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getPrice(),
                product.getInventory(),
                product.getDescription(),
                product.getCategory() != null ? product.getCategory().getName() : "Unknown",
                product.getImages() != null
                        ? product.getImages().stream().map(ProductImage::getImageUrl).collect(Collectors.toList())
                        : Collections.emptyList()
        );
    }
}