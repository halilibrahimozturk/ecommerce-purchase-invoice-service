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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Invoice domain service implementation.
 *
 * <p>
 * This service is responsible for:
 * <ul>
 *   <li>Creating invoices</li>
 *   <li>Applying business rules (ownership, limits, duplication)</li>
 *   <li>Managing invoice lifecycle (approve, reject, cancel)</li>
 *   <li>Triggering notification events</li>
 * </ul>
 *
 * <p>
 * This class contains business logic only and does not deal with
 * transport-layer concerns (HTTP, DTO validation, etc.).
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final InvoiceMapper invoiceMapper;
    private final NotificationService notificationService;
    private final InvoiceApprovalProperties approvalProperties;

    /**
     * Creates a new invoice for the currently authenticated user.
     *
     * <p>
     * Business rules:
     * <ul>
     *   <li>User can only create invoice with own identity</li>
     *   <li>Bill number must be unique among approved invoices</li>
     *   <li>Total approved amount must not exceed configured limit</li>
     * </ul>
     *
     * @param request invoice creation request
     * @return created invoice response
     */
    @Override
    public InvoiceResponse createInvoice(InvoiceRequest request) {

        log.info("Creating invoice for billNo={}, email={}", request.billNo(), request.email());

        User currentUser = getCurrentUser();
        validateOwnership(request, currentUser);
        validateDuplicateBillNo(request.billNo());

        Product product = getProduct(request.productName());

        Invoice invoice = invoiceMapper.toEntity(request);
        invoice.assignTo(currentUser, product);

        BigDecimal newTotalAmount = calculateNewTotal(request.email(), request.amount());
        decideInvoiceStatus(invoice, newTotalAmount);

        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Invoice saved with id={}, status={}", savedInvoice.getId(), savedInvoice.getStatus());

        sendNotificationIfNeeded(savedInvoice);

        return invoiceMapper.toResponse(savedInvoice);
    }

    /**
     * Retrieves invoice details by invoice id.
     *
     * @param id invoice id
     * @return invoice response
     */
    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceById(String id) {
        return invoiceMapper.toResponse(findInvoiceById(id));
    }

    /**
     * Lists invoices of the current user filtered by status.
     *
     * @param status invoice status
     * @return list of invoice responses
     */
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

    /**
     * Lists all invoices filtered by status.
     * Intended for finance or admin use cases.
     *
     * @param status invoice status
     * @return list of invoice responses
     */
    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoicesByStatus(InvoiceStatus status) {

        return invoiceRepository
                .findByStatus(status)
                .stream()
                .map(invoiceMapper::toResponse)
                .toList();
    }

    /**
     * Cancels an invoice if business rules allow it.
     *
     * @param id invoice id
     */
    @Override
    public void cancelInvoice(Long id) {

        log.info("Attempting to cancel invoice id={}", id);

        Invoice invoice = findInvoiceById(String.valueOf(id));
        validateCancellationPermission(invoice);

        invoice.cancel();
        invoiceRepository.save(invoice);

        log.info("Invoice cancelled successfully. id={}", id);

        notificationService.notifyInvoiceCancelled(invoice);
    }

    /**
     * Retrieves the currently authenticated user from security context.
     */
    private User getCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    /**
     * Ensures invoice request belongs to the authenticated user.
     */
    private void validateOwnership(InvoiceRequest request, User user) {
        if (!user.matchesIdentity(request.email(), request.firstName(), request.lastName())) {
            log.warn("Invoice ownership validation failed for email={}", user.getEmail());
            throw new InvoiceOwnershipException();
        }
    }

    /**
     * Prevents duplicate bill numbers among approved invoices.
     */
    private void validateDuplicateBillNo(String billNo) {
        if (invoiceRepository.existsByBillNoAndStatus(billNo, InvoiceStatus.APPROVED)) {
            log.warn("Duplicate approved billNo detected: {}", billNo);
            throw new DuplicateBillNoException(billNo);
        }
    }

    /**
     * Fetches product by name.
     */
    private Product getProduct(String productName) {
        return productRepository
                .findByName(productName)
                .orElseThrow(() -> new ProductNotFoundException(productName));
    }

    /**
     * Calculates new total approved amount for a user.
     */
    private BigDecimal calculateNewTotal(String email, BigDecimal amount) {
        BigDecimal currentTotal =
                invoiceRepository.sumApprovedAmountByEmail(email);

        return currentTotal.add(amount);
    }

    /**
     * Determines invoice status based on approval limit.
     */
    private void decideInvoiceStatus(Invoice invoice, BigDecimal newTotalAmount) {
        if (newTotalAmount.compareTo(approvalProperties.getMaxLimit()) > 0) {
            log.info("Invoice rejected due to limit. invoiceId={}", invoice.getId());
            invoice.reject();
        } else {
            invoice.approve();
        }
    }

    /**
     * Sends notification only for rejected invoices.
     */
    private void sendNotificationIfNeeded(Invoice invoice) {
        if (invoice.getStatus() == InvoiceStatus.REJECTED) {
            notificationService.notifyInvoiceRejected(invoice);
        }
    }

    /**
     * Finds invoice by id or throws exception.
     */
    private Invoice findInvoiceById(String id) {
        return invoiceRepository
                .findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException(Long.valueOf(id)));
    }

    /**
     * Validates whether current user can cancel the invoice.
     */
    private void validateCancellationPermission(Invoice invoice) {

        String currentUserEmail = SecurityUtil.getCurrentUserEmail();

        if (!invoice.isOwnedBy(currentUserEmail)) {
            log.warn("Cancel attempt by non-owner. invoiceId={}, email={}",
                    invoice.getId(), currentUserEmail);
            throw new InvoiceOwnershipException();
        }

        if (!invoice.isCancellable()) {
            throw new InvoiceCannotBeCancelledException(
                    "Invoice cannot be cancelled in status: " + invoice.getStatus()
            );
        }
    }
}
