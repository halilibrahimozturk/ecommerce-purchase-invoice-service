package com.ozturk.purchaseinvoiceservice.mapper;

import com.ozturk.purchaseinvoiceservice.dto.request.ProductRequest;
import com.ozturk.purchaseinvoiceservice.dto.response.ProductResponse;
import com.ozturk.purchaseinvoiceservice.model.entity.Product;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(ProductRequest request);

    ProductResponse toResponse(Product product);

    List<ProductResponse> toResponseList(List<Product> list);

}