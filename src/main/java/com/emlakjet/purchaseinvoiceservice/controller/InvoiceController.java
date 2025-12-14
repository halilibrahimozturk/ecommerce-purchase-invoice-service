package com.emlakjet.purchaseinvoiceservice.controller;

import com.emlakjet.purchaseinvoiceservice.dto.request.InvoiceRequest;
import com.emlakjet.purchaseinvoiceservice.dto.response.ApiResponse;
import com.emlakjet.purchaseinvoiceservice.dto.response.InvoiceResponse;
import com.emlakjet.purchaseinvoiceservice.model.InvoiceStatus;
import com.emlakjet.purchaseinvoiceservice.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
            summary = "Cancel invoice",
            description = """
    Cancels an invoice.
    - Only the owner can cancel
    - Approved invoices cannot be cancelled
    """
    )
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelInvoice(@PathVariable Long id) {
        invoiceService.cancelInvoice(id);
        return ResponseEntity.ok(ApiResponse.success("Invoice cancelled", null));
    }
}
