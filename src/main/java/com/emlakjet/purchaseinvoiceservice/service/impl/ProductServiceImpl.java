package com.emlakjet.purchaseinvoiceservice.service.impl;

import com.emlakjet.purchaseinvoiceservice.dto.request.ProductRequest;
import com.emlakjet.purchaseinvoiceservice.dto.response.ProductResponse;
import com.emlakjet.purchaseinvoiceservice.mapper.ProductMapper;
import com.emlakjet.purchaseinvoiceservice.model.entity.Product;
import com.emlakjet.purchaseinvoiceservice.repository.ProductRepository;
import com.emlakjet.purchaseinvoiceservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse createProduct(ProductRequest request) {

        Product product = new Product();
        product.setName(request.name());
        product.setPrice(request.price());

        Product savedProduct = productRepository.save(product);

        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        if (request.name() != null) {
            product.setName(request.name());
        }

        if (request.price() != null) {
            product.setPrice(request.price());
        }

        Product updatedProduct = productRepository.save(product);

        return productMapper.toResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {

        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }

        productRepository.deleteById(id);
    }
}
