package com.emlakjet.purchaseinvoiceservice.service.impl;

import com.emlakjet.purchaseinvoiceservice.dto.request.ProductRequest;
import com.emlakjet.purchaseinvoiceservice.dto.response.ProductResponse;
import com.emlakjet.purchaseinvoiceservice.exception.ProductAlreadyExistsException;
import com.emlakjet.purchaseinvoiceservice.exception.ProductNotFoundException;
import com.emlakjet.purchaseinvoiceservice.mapper.ProductMapper;
import com.emlakjet.purchaseinvoiceservice.model.entity.Product;
import com.emlakjet.purchaseinvoiceservice.repository.ProductRepository;
import com.emlakjet.purchaseinvoiceservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Product domain service implementation.
 *
 * <p>
 * Responsible for managing product lifecycle operations such as:
 * <ul>
 *   <li>Creating products</li>
 *   <li>Updating product details</li>
 *   <li>Retrieving products (with caching)</li>
 *   <li>Deleting products</li>
 * </ul>
 *
 * <p>
 * Caching strategy:
 * <ul>
 *   <li>{@code products} → list of all products</li>
 *   <li>{@code productsById} → individual product lookup</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    /**
     * Creates a new product.
     *
     * <p>
     * Business rule:
     * <ul>
     *   <li>Product name must be unique</li>
     * </ul>
     *
     * <p>
     * Cache behavior:
     * <ul>
     *   <li>Evicts all {@code products} cache entries</li>
     * </ul>
     *
     * @param request product creation request
     * @return created product response
     */
    @Override
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse createProduct(ProductRequest request) {

        log.info("Creating product with name={}", request.name());

        if (productRepository.existsByName(request.name())) {
            log.warn("Product creation failed. Product already exists: {}", request.name());
            throw new ProductAlreadyExistsException(request.name());
        }

        Product product = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);

        log.info("Product created successfully. id={}, name={}",
                savedProduct.getId(), savedProduct.getName());

        return productMapper.toResponse(savedProduct);
    }

    /**
     * Retrieves a product by id.
     *
     * <p>
     * Cache behavior:
     * <ul>
     *   <li>Caches result in {@code productsById}</li>
     * </ul>
     *
     * @param id product id
     * @return product response
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "productsById", key = "#id")
    public ProductResponse getProductById(Long id) {

        log.debug("Fetching product by id={}", id);

        return productRepository.findById(id)
                .map(productMapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("Product not found. id={}", id);
                    return new ProductNotFoundException(id);
                });
    }

    /**
     * Retrieves all products.
     *
     * <p>
     * Cache behavior:
     * <ul>
     *   <li>Caches full product list in {@code products}</li>
     * </ul>
     *
     * @return list of product responses
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable("products")
    public List<ProductResponse> getAllProducts() {

        log.debug("Fetching all products");

        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    /**
     * Updates an existing product.
     *
     * <p>
     * Business rules:
     * <ul>
     *   <li>Product must exist</li>
     *   <li>New name must be unique (if changed)</li>
     * </ul>
     *
     * <p>
     * Cache behavior:
     * <ul>
     *   <li>Updates {@code productsById}</li>
     *   <li>Evicts all {@code products} cache entries</li>
     * </ul>
     *
     * @param id product id
     * @param request update request
     * @return updated product response
     */
    @Override
    @CachePut(value = "productsById", key = "#id")
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse updateProduct(Long id, ProductRequest request) {

        log.info("Updating product. id={}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product update failed. Product not found. id={}", id);
                    return new ProductNotFoundException(id);
                });

        // Name uniqueness is checked only if the name is actually being changed
        if (!product.getName().equals(request.name())
                && productRepository.existsByName(request.name())) {

            log.warn("Product update failed. Duplicate product name: {}", request.name());
            throw new ProductAlreadyExistsException(request.name());
        }

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());

        Product updatedProduct = productRepository.save(product);

        log.info("Product updated successfully. id={}, name={}",
                updatedProduct.getId(), updatedProduct.getName());

        return productMapper.toResponse(updatedProduct);
    }

    /**
     * Deletes a product by id.
     *
     * <p>
     * Cache behavior:
     * <ul>
     *   <li>Evicts all entries from {@code products} and {@code productsById}</li>
     * </ul>
     *
     * @param id product id
     */
    @Override
    @CacheEvict(value = {"products", "productsById"}, allEntries = true)
    public void deleteProduct(Long id) {

        log.info("Deleting product. id={}", id);

        if (!productRepository.existsById(id)) {
            log.warn("Product deletion failed. Product not found. id={}", id);
            throw new ProductNotFoundException(id);
        }

        productRepository.deleteById(id);

        log.info("Product deleted successfully. id={}", id);
    }
}
