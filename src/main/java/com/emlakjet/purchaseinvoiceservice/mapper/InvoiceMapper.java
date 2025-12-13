package com.emlakjet.purchaseinvoiceservice.mapper;

import com.emlakjet.purchaseinvoiceservice.dto.request.InvoiceRequest;
import com.emlakjet.purchaseinvoiceservice.dto.response.InvoiceResponse;
import com.emlakjet.purchaseinvoiceservice.model.entity.Invoice;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    Invoice toEntity(InvoiceRequest request);

    InvoiceResponse toResponse(Invoice invoice);

    List<InvoiceResponse> toResponseList(List<Invoice> list);
}