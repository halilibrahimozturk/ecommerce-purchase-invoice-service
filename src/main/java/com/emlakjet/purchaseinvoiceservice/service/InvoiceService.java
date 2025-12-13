package com.emlakjet.purchaseinvoiceservice.service;

import com.emlakjet.purchaseinvoiceservice.dto.request.InvoiceRequest;
import com.emlakjet.purchaseinvoiceservice.dto.response.InvoiceResponse;
import com.emlakjet.purchaseinvoiceservice.model.InvoiceStatus;

import java.math.BigDecimal;
import java.util.List;

public interface InvoiceService {

    InvoiceResponse createInvoice(InvoiceRequest invoiceRequest);

    InvoiceResponse getInvoiceById(String id);

    List<InvoiceResponse> listInvoices(String status, String email, String firstName, String lastName);

    List<InvoiceResponse> getInvoicesByStatus(InvoiceStatus status);

    InvoiceResponse updateInvoice(Long id, InvoiceRequest invoiceRequest);

    void deleteInvoice(Long id);

    BigDecimal getTotalApprovedAmountByPurchaser(String email);
}