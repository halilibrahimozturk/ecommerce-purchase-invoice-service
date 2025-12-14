package com.emlakjet.purchaseinvoiceservice.mapper;

import com.emlakjet.purchaseinvoiceservice.dto.request.InvoiceRequest;
import com.emlakjet.purchaseinvoiceservice.dto.response.InvoiceResponse;
import com.emlakjet.purchaseinvoiceservice.model.entity.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    @Mapping(target = "purchasingSpecialist", ignore = true)
    @Mapping(target = "product", ignore = true)
    Invoice toEntity(InvoiceRequest request);

    @Mapping(source = "purchasingSpecialist.email", target = "email")
    @Mapping(source = "purchasingSpecialist.firstName", target = "firstName")
    @Mapping(source = "purchasingSpecialist.lastName", target = "lastName")
    @Mapping(source = "product.name", target = "productName")
    InvoiceResponse toResponse(Invoice invoice);

    List<InvoiceResponse> toResponseList(List<Invoice> list);
}