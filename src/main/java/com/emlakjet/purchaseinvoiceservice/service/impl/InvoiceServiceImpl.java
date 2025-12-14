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
import com.emlakjet.purchaseinvoiceservice.model.entity.PurchasingSpecialist;
import com.emlakjet.purchaseinvoiceservice.repository.InvoiceRepository;
import com.emlakjet.purchaseinvoiceservice.repository.ProductRepository;
import com.emlakjet.purchaseinvoiceservice.repository.PurchasingSpecialistRepository;
import com.emlakjet.purchaseinvoiceservice.service.InvoiceService;
import com.emlakjet.purchaseinvoiceservice.service.NotificationService;
import com.emlakjet.purchaseinvoiceservice.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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
    private final NotificationService notificationService;
    private final PurchasingSpecialistRepository specialistRepository;
    private final InvoiceApprovalProperties approvalProperties;


    @Override
    public InvoiceResponse createInvoice(InvoiceRequest invoiceRequest) {
        String loggedEmail = SecurityUtil.getCurrentUserEmail();

        PurchasingSpecialist user =
                specialistRepository.findByEmail(loggedEmail)
                        .orElseThrow(() -> new RuntimeException("User not found"));

        validateInvoiceOwner(invoiceRequest, user);

        if (invoiceRepository.existsByBillNo(invoiceRequest.billNo())) {
            throw new DuplicateBillNoException(invoiceRequest.productName());
        }

        productRepository.findByName(invoiceRequest.productName())
                .orElseThrow(() -> new ProductNotFoundException(invoiceRequest.productName()));

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
    public List<InvoiceResponse> listInvoices(InvoiceStatus status) {
        String email = SecurityUtil.getCurrentUserEmail();
        return invoiceRepository.findByStatusAndPurchasingSpecialist_Email(status, email)
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
    public void cancelInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(String.valueOf(id))
                .orElseThrow(() -> new InvoiceNotFoundException(id));

        String currentUserEmail = SecurityUtil.getCurrentUserEmail();

        if (!invoice.getPurchasingSpecialist().getEmail().equals(currentUserEmail)) {
            throw new AccessDeniedException("You can only cancel your own invoices");
        }

        if (invoice.getStatus() == InvoiceStatus.APPROVED) {
            throw new IllegalStateException("Approved invoices cannot be cancelled");
        }

        invoice.setStatus(InvoiceStatus.CANCELLED);
        invoiceRepository.save(invoice);
    }

    private void validateInvoiceOwner(InvoiceRequest request, PurchasingSpecialist user) {

        if (!request.email().equals(user.getEmail())
                || !request.firstName().equals(user.getFirstName())
                || !request.lastName().equals(user.getLastName())) {

            throw new AccessDeniedException(
                    "Invoice can only be created with your own identity information"
            );
        }
    }
}