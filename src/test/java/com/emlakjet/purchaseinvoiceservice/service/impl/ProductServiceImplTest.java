package com.emlakjet.purchaseinvoiceservice.service.impl;

import com.emlakjet.purchaseinvoiceservice.dto.request.ProductRequest;
import com.emlakjet.purchaseinvoiceservice.dto.response.ProductResponse;
import com.emlakjet.purchaseinvoiceservice.exception.ProductAlreadyExistsException;
import com.emlakjet.purchaseinvoiceservice.exception.ProductNotFoundException;
import com.emlakjet.purchaseinvoiceservice.mapper.ProductMapper;
import com.emlakjet.purchaseinvoiceservice.model.entity.Product;
import com.emlakjet.purchaseinvoiceservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    private Product product;
    private ProductRequest request;
    private ProductResponse response;

    @BeforeEach
    void setup() {

        request = new ProductRequest(
                "Laptop",
                "Gaming laptop",
                BigDecimal.valueOf(1000)
        );

        product = Product.builder()
                .id(1L)
                .name("Laptop")
                .description("Gaming laptop")
                .price(BigDecimal.valueOf(1000))
                .build();

        response = new ProductResponse(
                "1",
                "Laptop",
                "Gaming laptop",
                BigDecimal.valueOf(1000)
        );
    }

    @Test
    void createProduct_shouldCreateSuccessfully() {

        when(productRepository.existsByName("Laptop")).thenReturn(false);
        when(productMapper.toEntity(request)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(response);

        ProductResponse result = productService.createProduct(request);

        assertNotNull(result);
        assertEquals("Laptop", result.name());
        verify(productRepository).save(product);
    }

    @Test
    void createProduct_shouldThrow_whenNameAlreadyExists() {

        when(productRepository.existsByName("Laptop")).thenReturn(true);

        assertThrows(ProductAlreadyExistsException.class,
                () -> productService.createProduct(request));

        verify(productRepository, never()).save(any());
    }

    @Test
    void getProductById_shouldReturnProduct() {

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(response);

        ProductResponse result = productService.getProductById(1L);

        assertEquals("Laptop", result.name());
    }

    @Test
    void getProductById_shouldThrow_whenNotFound() {

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> productService.getProductById(1L));
    }

    @Test
    void getAllProducts_shouldReturnList() {

        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productMapper.toResponse(product)).thenReturn(response);

        List<ProductResponse> result = productService.getAllProducts();

        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).name());
    }

    @Test
    void updateProduct_shouldUpdateSuccessfully() {

        ProductRequest updateRequest = new ProductRequest(
                "Laptop Pro",
                "Updated",
                BigDecimal.valueOf(1500)
        );

        Product updated = Product.builder()
                .id(1L)
                .name("Laptop Pro")
                .description("Updated")
                .price(BigDecimal.valueOf(1500))
                .build();

        ProductResponse updatedResponse = new ProductResponse(
                "1", "Laptop Pro", "Updated", BigDecimal.valueOf(1500)
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.existsByName("Laptop Pro")).thenReturn(false);
        when(productRepository.save(any())).thenReturn(updated);
        when(productMapper.toResponse(updated)).thenReturn(updatedResponse);

        ProductResponse result = productService.updateProduct(1L, updateRequest);

        assertEquals("Laptop Pro", result.name());
    }

    @Test
    void updateProduct_shouldThrow_whenProductNotFound() {

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> productService.updateProduct(1L, request));
    }

    @Test
    void updateProduct_shouldThrow_whenNameConflict() {

        ProductRequest updateRequest = new ProductRequest(
                "Tablet",
                "Another",
                BigDecimal.valueOf(500)
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.existsByName("Tablet")).thenReturn(true);

        assertThrows(ProductAlreadyExistsException.class,
                () -> productService.updateProduct(1L, updateRequest));
    }

    @Test
    void deleteProduct_shouldDeleteSuccessfully() {

        when(productRepository.existsById(1L)).thenReturn(true);

        productService.deleteProduct(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test
    void deleteProduct_shouldThrow_whenNotFound() {

        when(productRepository.existsById(1L)).thenReturn(false);

        assertThrows(ProductNotFoundException.class,
                () -> productService.deleteProduct(1L));
    }

}
