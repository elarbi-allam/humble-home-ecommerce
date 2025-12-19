package com.elarbiallam.product.service;

import com.elarbiallam.product.dto.ProductRequest;
import com.elarbiallam.product.dto.ProductResponse;
import com.elarbiallam.product.entity.Category;
import com.elarbiallam.product.entity.Product;
import com.elarbiallam.product.entity.ProductImage;
import com.elarbiallam.product.exception.EntityNotFoundException; // À créer
import com.elarbiallam.product.mapper.ProductMapper;
import com.elarbiallam.product.repository.CategoryRepository;
import com.elarbiallam.product.repository.ProductImageRepository;
import com.elarbiallam.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository imageRepository;
    private final ProductMapper productMapper;

    // --- LECTURE (PUBLIC) ---

    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toResponse);
    }

    public ProductResponse getProductById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Produit non trouvé avec l'ID : " + id));
    }

    // --- ECRITURE (ADMIN) ---

    @Transactional
    public Long createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Catégorie non trouvée"));

        Product product = productMapper.toEntity(request, category);
        Product savedProduct = productRepository.save(product);
        return savedProduct.getId();
    }

    @Transactional
    public void uploadImage(Long productId, MultipartFile file) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Produit non trouvé"));

        try {
            // 1. Sauvegarde Physique (Dossier 'uploads')
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get("uploads");
            if (!Files.exists(path)) Files.createDirectories(path);
            Files.copy(file.getInputStream(), path.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

            // 2. Sauvegarde Base de Données
            String imageUrl = "/api/images/" + fileName; // URL pour le frontend
            ProductImage image = ProductImage.builder()
                    .imageUrl(imageUrl)
                    .product(product)
                    .build();

            imageRepository.save(image);

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'upload de l'image", e);
        }
    }

    // update et delete à implémenter de la même façon...
}