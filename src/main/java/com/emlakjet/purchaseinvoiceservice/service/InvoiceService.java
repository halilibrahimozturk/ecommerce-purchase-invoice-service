package com.ozturk.purchaseinvoiceservice.service;

import com.ozturk.purchaseinvoiceservice.dto.request.InvoiceRequest;
import com.ozturk.purchaseinvoiceservice.dto.response.InvoiceResponse;
import com.ozturk.purchaseinvoiceservice.exception.DuplicateBillNoException;
import com.ozturk.purchaseinvoiceservice.exception.InvoiceCannotBeCancelledException;
import com.ozturk.purchaseinvoiceservice.exception.InvoiceNotFoundException;
import com.ozturk.purchaseinvoiceservice.exception.InvoiceOwnershipException;
import com.ozturk.purchaseinvoiceservice.model.InvoiceStatus;

import java.util.List;

/**
 * Service for managing invoices.
 *
 * <p>Provides operations for creating, retrieving, listing, and cancelling invoices.
 */
public interface InvoiceService {

    /**
     * Creates a new invoice for the authenticated user.
     *
     * @param invoiceRequest invoice creation details
     * @return created invoice response
     * @throws InvoiceOwnershipException if user info mismatch
     * @throws DuplicateBillNoException if bill number is already approved
     */
    InvoiceResponse createInvoice(InvoiceRequest invoiceRequest);

    /**
     * Retrieves an invoice by its ID.
     *
     * @param id invoice ID
     * @return invoice response
     * @throws InvoiceNotFoundException if invoice does not exist
     */
    InvoiceResponse getInvoiceById(String id);

    /**
     * Lists invoices of the authenticated user filtered by status.
     *
     * @param status invoice status
     * @return list of invoices
     */
    List<InvoiceResponse> listInvoices(InvoiceStatus status);

    /**
     * Lists all invoices filtered by status (for finance/admin purposes).
     *
     * @param status invoice status
     * @return list of invoices
     */
    List<InvoiceResponse> getInvoicesByStatus(InvoiceStatus status);

    /**
     * Cancels an invoice if permitted.
     *
     * @param id invoice ID
     * @throws InvoiceOwnershipException if current user is not owner
     * @throws InvoiceCannotBeCancelledException if invoice cannot be cancelled
     */
    void cancelInvoice(Long id);
}
