package com.emlakjet.purchaseinvoiceservice.service.impl;

import com.emlakjet.purchaseinvoiceservice.config.InvoiceApprovalProperties;
import com.emlakjet.purchaseinvoiceservice.dto.request.InvoiceRequest;
import com.emlakjet.purchaseinvoiceservice.dto.response.InvoiceResponse;
import com.emlakjet.purchaseinvoiceservice.exception.DuplicateBillNoException;
import com.emlakjet.purchaseinvoiceservice.exception.InvoiceNotFoundException;
import com.emlakjet.purchaseinvoiceservice.exception.ProductNotFoundException;
import com.emlakjet.purchaseinvoiceservice.mapper.InvoiceMapper;
import com.emlakjet.purchaseinvoiceservice.model.InvoiceStatus;
import com.emlakjet.purchaseinvoiceservice.model.entity.Invoice;
import com.emlakjet.purchaseinvoiceservice.model.entity.Product;
import com.emlakjet.purchaseinvoiceservice.repository.InvoiceRepository;
import com.emlakjet.purchaseinvoiceservice.repository.ProductRepository;
import com.emlakjet.purchaseinvoiceservice.service.InvoiceService;
import com.emlakjet.purchaseinvoiceservice.service.NotificationService;
import com.emlakjet.purchaseinvoiceservice.service.ProductService;
import com.emlakjet.purchaseinvoiceservice.service.rule.InvoiceApprovalRuleEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ProductRepository productRepository;
    private final InvoiceMapper invoiceMapper;
    private final InvoiceApprovalRuleEngine invoiceApprovalRuleEngine;
    private final ProductService productService;
    private final NotificationService notificationService;

    private final InvoiceApprovalProperties approvalProperties;


    @Override
    public InvoiceResponse createInvoice(InvoiceRequest invoiceRequest) {

        Product product = productRepository.findByName(invoiceRequest.productName())
                .orElseThrow(() -> new ProductNotFoundException(invoiceRequest.productName()));

        if (invoiceRepository.existsByBillNo(invoiceRequest.billNo())) {
            throw new DuplicateBillNoException(invoiceRequest.productName());
        }

        BigDecimal currentTotal = invoiceRepository.sumApprovedAmountByEmail(invoiceRequest.email());

        BigDecimal newTotal = currentTotal.add(invoiceRequest.amount());

        Invoice invoice = invoiceMapper.toEntity(invoiceRequest);

        if (!isAmountApproved(newTotal)) {

            invoice.setStatus(InvoiceStatus.REJECTED);
            Invoice saved = invoiceRepository.save(invoice);
            notificationService.notifyRejectedInvoice(invoice);

            return invoiceMapper.toResponse(saved);
        }

        invoice.setStatus(InvoiceStatus.APPROVED);

        Invoice saved = invoiceRepository.save(invoice);

        return invoiceMapper.toResponse(saved);
    }

    private boolean isAmountApproved(BigDecimal invoiceAmount) {
        return invoiceAmount.compareTo(approvalProperties.getMaxLimit()) <= 0;
    }

    @Override
    public InvoiceResponse getInvoiceById(String id) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(() -> new InvoiceNotFoundException(Long.valueOf(id)));
        return invoiceMapper.toResponse(invoice);
    }

    @Override
    public List<InvoiceResponse> listInvoices(String status, String email, String firstName, String lastName) {
        return invoiceRepository.findAllWithFilters(status, email, firstName, lastName)
                .stream()
                .map(invoiceMapper::toResponse)
                .toList();
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
                .orElseThrow(() -> new InvoiceNotFoundException(id));

        BigDecimal totalAmount =
                invoiceRepository.sumApprovedAmountByEmail(invoice.getPurchasingSpecialist().getEmail())
                        .subtract(invoice.getAmount());

        BigDecimal newTotal = totalAmount.add(invoiceRequest.amount());


        invoice.setBillNo(invoiceRequest.billNo());
        invoice.setAmount(invoiceRequest.amount());

        if (!isAmountApproved(newTotal)) {
            invoice.setStatus(InvoiceStatus.REJECTED);
            Invoice saved = invoiceRepository.save(invoice);
            notificationService.notifyRejectedInvoice(invoice);
            return invoiceMapper.toResponse(saved);
        }

        invoice.setStatus(InvoiceStatus.APPROVED);
        Invoice updated = invoiceRepository.save(invoice);
        return invoiceMapper.toResponse(updated);
    }


    @Override
    public void deleteInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(String.valueOf(id))
                .orElseThrow(() -> new InvoiceNotFoundException(id));

        invoiceRepository.delete(invoice);
    }

    @Override
    public BigDecimal getTotalApprovedAmountByPurchaser(String email) {
        return invoiceRepository.sumApprovedAmountByEmail(email);
    }
}