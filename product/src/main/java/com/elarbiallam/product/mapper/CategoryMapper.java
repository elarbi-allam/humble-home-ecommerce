package com.elarbiallam.product.mapper;

import com.elarbiallam.product.dto.CategoryRequest;
import com.elarbiallam.product.dto.CategoryResponse;
import com.elarbiallam.product.entity.Category;
import org.springframework.stereotype.Service;

@Service
public class CategoryMapper {

    public Category toEntity(CategoryRequest request) {
        return Category.builder()
                .name(request.name())
                .description(request.description())
                .build();
    }

    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}