package com.emlakjet.purchaseinvoiceservice.service.impl;

import com.emlakjet.purchaseinvoiceservice.dto.request.InvoiceRequest;
import com.emlakjet.purchaseinvoiceservice.dto.response.InvoiceResponse;
import com.emlakjet.purchaseinvoiceservice.mapper.InvoiceMapper;
import com.emlakjet.purchaseinvoiceservice.model.InvoiceStatus;
import com.emlakjet.purchaseinvoiceservice.model.entity.Invoice;
import com.emlakjet.purchaseinvoiceservice.repository.InvoiceRepository;
import com.emlakjet.purchaseinvoiceservice.service.InvoiceService;
import com.emlakjet.purchaseinvoiceservice.service.rule.InvoiceApprovalRuleEngine;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private final InvoiceApprovalRuleEngine invoiceApprovalRuleEngine;

    @Override
    public InvoiceResponse createInvoice(InvoiceRequest invoiceRequest) {

        // Request → Entity
        Invoice invoice = invoiceMapper.toEntity(invoiceRequest);

        invoice.setStatus(invoiceApprovalRuleEngine.evaluate(invoice));

        Invoice saved = invoiceRepository.save(invoice);

        return invoiceMapper.toResponse(saved);
    }

    @Override
    public InvoiceResponse getInvoiceById(String id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found: " + id));

        return invoiceMapper.toResponse(invoice);
    }

    @Override
    public List<InvoiceResponse> listInvoices(
            String status,
            String email,
            String firstName,
            String lastName
    ) {
        List<Invoice> invoices = invoiceRepository.findAllWithFilters(status, email, firstName, lastName);
        return invoiceMapper.toResponseList(invoices);
    }

    @Override
    public List<InvoiceResponse> getInvoicesByStatus(InvoiceStatus status) {
        return invoiceRepository.findByStatus(status)
                .stream()
                .map(invoiceMapper::toResponse)
                .toList();
    }

    @Override
    public InvoiceResponse updateInvoice(Long id, InvoiceRequest invoiceRequest) {

        Invoice invoice = invoiceRepository.findById(String.valueOf(id))
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found: " + id));

        // Update allowed fields
        invoice.setBillNo(invoiceRequest.billNo());
        invoice.setAmount(invoiceRequest.amount());

        invoice.setStatus(invoiceApprovalRuleEngine.evaluate(invoice));

        Invoice updated = invoiceRepository.save(invoice);
        return invoiceMapper.toResponse(updated);
    }


    @Override
    public void deleteInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(String.valueOf(id))
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found: " + id));

        invoiceRepository.delete(invoice);
    }
}