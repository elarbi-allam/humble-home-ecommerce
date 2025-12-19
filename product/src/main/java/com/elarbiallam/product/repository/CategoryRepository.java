package com.elarbiallam.product.repository;

import com.elarbiallam.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Vérification de l'unicité du nom de la catégorie
    boolean existsByName(String name);
}