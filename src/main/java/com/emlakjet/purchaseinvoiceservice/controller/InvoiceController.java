package com.emlakjet.purchaseinvoiceservice.controller;

import com.emlakjet.purchaseinvoiceservice.dto.request.InvoiceRequest;
import com.emlakjet.purchaseinvoiceservice.dto.response.ApiResponse;
import com.emlakjet.purchaseinvoiceservice.dto.response.InvoiceResponse;
import com.emlakjet.purchaseinvoiceservice.model.InvoiceStatus;
import com.emlakjet.purchaseinvoiceservice.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<ApiResponse<InvoiceResponse>> createInvoice(@Valid @RequestBody InvoiceRequest invoiceRequest) {
        InvoiceResponse invoiceResponse = invoiceService.createInvoice(invoiceRequest);

        if (invoiceResponse.status() == InvoiceStatus.APPROVED) {
            return ResponseEntity.ok(ApiResponse.success("Invoice accepted", invoiceResponse));
        } else {
            return ResponseEntity.status(422).body(ApiResponse.error("Invoice rejected: limit exceeded"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoiceById(@PathVariable String id) {
        InvoiceResponse invoiceResponse = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(ApiResponse.success("Invoice: ", invoiceResponse));
    }

    /**
     * LIST ALL INVOICES (with filters)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> listInvoices(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName) {

        List<InvoiceResponse> invoices = invoiceService.listInvoices(status, email, firstName, lastName);
        return ResponseEntity.ok(ApiResponse.success("Invoices: ", invoices));
    }

    @GetMapping("/approved")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> getApprovedInvoices() {
        List<InvoiceResponse> list = invoiceService.getInvoicesByStatus(InvoiceStatus.APPROVED);
        return ResponseEntity.ok(ApiResponse.success("Approved invoices", list));
    }


    @GetMapping("/rejected")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> getRejectedInvoices() {
        List<InvoiceResponse> list = invoiceService.getInvoicesByStatus(InvoiceStatus.REJECTED);
        return ResponseEntity.ok(ApiResponse.success("Rejected invoices", list));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InvoiceResponse>> update(@Valid
                                                               @PathVariable Long id,
                                                               @RequestBody InvoiceRequest invoiceRequest
    ) {
        InvoiceResponse invoiceResponse = invoiceService.updateInvoice(id, invoiceRequest);

        if (invoiceResponse.status() == InvoiceStatus.APPROVED) {
            return ResponseEntity.ok(ApiResponse.success("Invoice updated", invoiceResponse));
        } else {
            return ResponseEntity.status(422).body(ApiResponse.error("Invoice not updated: limit exceeded"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }
}
