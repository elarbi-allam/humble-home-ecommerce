package com.elarbiallam.product.repository;

import com.elarbiallam.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Pagination native grâce à Pageable
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
}