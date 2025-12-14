package com.emlakjet.purchaseinvoiceservice.service;

import com.emlakjet.purchaseinvoiceservice.dto.request.InvoiceRequest;
import com.emlakjet.purchaseinvoiceservice.dto.response.InvoiceResponse;
import com.emlakjet.purchaseinvoiceservice.model.InvoiceStatus;

import java.util.List;

public interface InvoiceService {

    InvoiceResponse createInvoice(InvoiceRequest invoiceRequest);

    InvoiceResponse getInvoiceById(String id);

    List<InvoiceResponse> listInvoices(InvoiceStatus status);

    List<InvoiceResponse> getInvoicesByStatus(InvoiceStatus status);

    void cancelInvoice(Long id);

}