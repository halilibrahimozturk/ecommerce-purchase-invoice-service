package com.emlakjet.purchaseinvoiceservice.service.impl;

import com.emlakjet.purchaseinvoiceservice.config.InvoiceApprovalProperties;
import com.emlakjet.purchaseinvoiceservice.dto.request.InvoiceRequest;
import com.emlakjet.purchaseinvoiceservice.dto.response.InvoiceResponse;
import com.emlakjet.purchaseinvoiceservice.exception.*;
import com.emlakjet.purchaseinvoiceservice.mapper.InvoiceMapper;
import com.emlakjet.purchaseinvoiceservice.model.InvoiceStatus;
import com.emlakjet.purchaseinvoiceservice.model.entity.Invoice;
import com.emlakjet.purchaseinvoiceservice.model.entity.Product;
import com.emlakjet.purchaseinvoiceservice.model.entity.User;
import com.emlakjet.purchaseinvoiceservice.repository.InvoiceRepository;
import com.emlakjet.purchaseinvoiceservice.repository.ProductRepository;
import com.emlakjet.purchaseinvoiceservice.repository.UserRepository;
import com.emlakjet.purchaseinvoiceservice.service.InvoiceService;
import com.emlakjet.purchaseinvoiceservice.service.NotificationService;
import com.emlakjet.purchaseinvoiceservice.util.SecurityUtil;
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
    private final UserRepository userRepository;
    private final InvoiceMapper invoiceMapper;
    private final NotificationService notificationService;
    private final InvoiceApprovalProperties approvalProperties;

    @Override
    public InvoiceResponse createInvoice(InvoiceRequest request) {

        User currentUser = getCurrentUser();
        validateOwnership(request, currentUser);
        validateDuplicateBillNo(request.billNo());

        Product product = getProduct(request.productName());

        Invoice invoice = invoiceMapper.toEntity(request);
        invoice.assignTo(currentUser, product);

        BigDecimal newTotalAmount = calculateNewTotal(request.email(), request.amount());
        decideInvoiceStatus(invoice, newTotalAmount);

        Invoice savedInvoice = invoiceRepository.save(invoice);
        sendNotificationIfNeeded(savedInvoice);

        return invoiceMapper.toResponse(savedInvoice);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceById(String id) {
        return invoiceMapper.toResponse(findInvoiceById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponse> listInvoices(InvoiceStatus status) {
        String email = SecurityUtil.getCurrentUserEmail();
        return invoiceRepository
                .findByStatusAndPurchasingSpecialist_Email(status, email)
                .stream()
                .map(invoiceMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoicesByStatus(InvoiceStatus status) {
        return invoiceRepository
                .findByStatus(status)
                .stream()
                .map(invoiceMapper::toResponse)
                .toList();
    }

    @Override
    public void cancelInvoice(Long id) {

        Invoice invoice = findInvoiceById(String.valueOf(id));
        validateCancellationPermission(invoice);

        invoice.cancel();
        invoiceRepository.save(invoice);

        notificationService.notifyInvoiceCancelled(invoice);
    }

    private User getCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    private void validateOwnership(InvoiceRequest request, User user) {
        if (!user.matchesIdentity(request.email(), request.firstName(), request.lastName())) {
            throw new InvoiceOwnershipException();
        }
    }

    private void validateDuplicateBillNo(String billNo) {
        if (invoiceRepository.existsByBillNoAndStatus(billNo, InvoiceStatus.APPROVED)) {
            throw new DuplicateBillNoException(billNo);
        }
    }

    private Product getProduct(String productName) {
        return productRepository
                .findByName(productName)
                .orElseThrow(() -> new ProductNotFoundException(productName));
    }

    private BigDecimal calculateNewTotal(String email, BigDecimal amount) {
        BigDecimal currentTotal =
                invoiceRepository.sumApprovedAmountByEmail(email);

        return currentTotal.add(amount);
    }

    private void decideInvoiceStatus(Invoice invoice, BigDecimal newTotalAmount) {
        if (newTotalAmount.compareTo(approvalProperties.getMaxLimit()) > 0) {
            invoice.reject();
        } else {
            invoice.approve();
        }
    }

    private void sendNotificationIfNeeded(Invoice invoice) {
        if (invoice.getStatus() == InvoiceStatus.REJECTED) {
            notificationService.notifyInvoiceRejected(invoice);
        }
    }

    private Invoice findInvoiceById(String id) {
        return invoiceRepository
                .findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException(Long.valueOf(id)));
    }

    private void validateCancellationPermission(Invoice invoice) {

        String currentUserEmail = SecurityUtil.getCurrentUserEmail();

        if (!invoice.isOwnedBy(currentUserEmail)) {
            throw new InvoiceOwnershipException();
        }

        if (!invoice.isCancellable()) {
            throw new InvoiceCannotBeCancelledException(
                    "Invoice cannot be cancelled in status: " + invoice.getStatus()
            );
        }
    }
}
