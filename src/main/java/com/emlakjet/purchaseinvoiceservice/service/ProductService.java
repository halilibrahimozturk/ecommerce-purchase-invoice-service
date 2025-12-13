package com.emlakjet.purchaseinvoiceservice.service;


import com.emlakjet.purchaseinvoiceservice.dto.request.ProductRequest;
import com.emlakjet.purchaseinvoiceservice.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    ProductResponse getProductById(Long id);

    List<ProductResponse> getAllProducts();

    ProductResponse updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);
}