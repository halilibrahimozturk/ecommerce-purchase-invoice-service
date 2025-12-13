package com.emlakjet.purchaseinvoiceservice.mapper;

import com.emlakjet.purchaseinvoiceservice.dto.request.ProductRequest;
import com.emlakjet.purchaseinvoiceservice.dto.response.ProductResponse;
import com.emlakjet.purchaseinvoiceservice.model.entity.Product;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(ProductRequest request);

    ProductResponse toResponse(Product product);

    List<ProductResponse> toResponseList(List<Product> list);

}