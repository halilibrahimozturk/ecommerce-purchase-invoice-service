package com.emlakjet.purchaseinvoiceservice.controller;

import com.emlakjet.purchaseinvoiceservice.dto.request.ProductRequest;
import com.emlakjet.purchaseinvoiceservice.dto.response.CommonApiResponse;
import com.emlakjet.purchaseinvoiceservice.dto.response.ProductResponse;
import com.emlakjet.purchaseinvoiceservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(
        name = "Products",
        description = "Product management operations"
)
public class ProductController {

    private final ProductService productService;

    @Operation(
            summary = "Create product",
            description = """
                    Creates a new product.

                    Rules:
                    - Product name must be unique
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Product created successfully",
            content = @Content(
                    schema = @Schema(implementation = ProductResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "409",
            description = "Product with the same name already exists",
            content = @Content
    )
    @PostMapping
    public ResponseEntity<CommonApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        ProductResponse productResponse = productService.createProduct(productRequest);
        return ResponseEntity.ok(CommonApiResponse.success("Product created", productResponse));
    }

    @Operation(
            summary = "Get all products",
            description = "Returns all products in the system"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Products retrieved successfully"
    )
    @GetMapping
    public ResponseEntity<CommonApiResponse<List<ProductResponse>>> getAllProducts() {
        return ResponseEntity.ok(CommonApiResponse.success("Products", productService.getAllProducts()));
    }

    @Operation(
            summary = "Get product by id",
            description = "Returns product details by product id"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Product found",
            content = @Content(
                    schema = @Schema(implementation = ProductResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content
    )
    @GetMapping("/{id}")
    public ResponseEntity<CommonApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
        ProductResponse productResponse = productService.getProductById(id);
        return ResponseEntity.ok(CommonApiResponse.success("Product detail", productResponse));
    }

    @Operation(
            summary = "Update product",
            description = """
                    Updates product information.

                    Rules:
                    - Product name must remain unique
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Product updated successfully",
            content = @Content(
                    schema = @Schema(implementation = ProductResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content
    )
    @ApiResponse(
            responseCode = "409",
            description = "Product with the same name already exists",
            content = @Content
    )
    @PutMapping("/{id}")
    public ResponseEntity<CommonApiResponse<ProductResponse>> updateProduct(@Valid @PathVariable Long id,
                                                                            @RequestBody ProductRequest productRequest) {
        ProductResponse productResponse = productService.updateProduct(id, productRequest);
        return ResponseEntity.ok(CommonApiResponse.success("Product updated", productResponse));
    }

    @Operation(
            summary = "Delete product",
            description = "Deletes product by id"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Product deleted successfully"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonApiResponse<?>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(CommonApiResponse.success("Product deleted", null));
    }
}