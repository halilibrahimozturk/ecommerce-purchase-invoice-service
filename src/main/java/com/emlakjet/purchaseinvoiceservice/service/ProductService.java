package com.emlakjet.purchaseinvoiceservice.service;

import com.emlakjet.purchaseinvoiceservice.dto.request.ProductRequest;
import com.emlakjet.purchaseinvoiceservice.dto.response.ProductResponse;

import java.util.List;

/**
 * Service for managing products.
 * Handles creation, update, retrieval, and deletion of products.
 */
public interface ProductService {

    /**
     * Creates a new product.
     *
     * @param request product creation request
     * @return created product response
     */
    ProductResponse createProduct(ProductRequest request);

    /**
     * Retrieves a product by id.
     *
     * @param id product id
     * @return product response
     */
    ProductResponse getProductById(Long id);

    /**
     * Retrieves all products.
     *
     * @return list of product responses
     */
    List<ProductResponse> getAllProducts();

    /**
     * Updates an existing product.
     *
     * @param id product id
     * @param request updated product data
     * @return updated product response
     */
    ProductResponse updateProduct(Long id, ProductRequest request);

    /**
     * Deletes a product by id.
     *
     * @param id product id
     */
    void deleteProduct(Long id);
}
