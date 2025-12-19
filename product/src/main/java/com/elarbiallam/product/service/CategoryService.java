package com.elarbiallam.product.service;

import com.elarbiallam.product.dto.CategoryRequest;
import com.elarbiallam.product.dto.CategoryResponse;
import com.elarbiallam.product.entity.Category;
import com.elarbiallam.product.mapper.CategoryMapper;
import com.elarbiallam.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public Long createCategory(CategoryRequest request) {
        // Validation basique
        if (categoryRepository.existsByName(request.name())) {
            throw new RuntimeException("Une catégorie avec ce nom existe déjà");
        }
        Category category = categoryMapper.toEntity(request);
        return categoryRepository.save(category).getId();
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
    }
}