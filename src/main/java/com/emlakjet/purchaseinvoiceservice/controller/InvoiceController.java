package com.emlakjet.purchaseinvoiceservice.controller;

import com.emlakjet.purchaseinvoiceservice.dto.request.InvoiceRequest;
import com.emlakjet.purchaseinvoiceservice.dto.response.ApiResponse;
import com.emlakjet.purchaseinvoiceservice.dto.response.InvoiceResponse;
import com.emlakjet.purchaseinvoiceservice.model.InvoiceStatus;
import com.emlakjet.purchaseinvoiceservice.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Tag(
        name = "Invoices",
        description = "Invoice creation, retrieval, approval and cancellation operations"
)
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Operation(
            summary = "Create invoice",
            description = """
                    Creates a new invoice for the logged-in purchasing specialist.

                    Business rules:
                    - Invoice can only be created with user's own identity information
                    - Bill number must be unique
                    - If total approved amount exceeds the configured limit, invoice is rejected
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Invoice approved",
            content = @Content(
                    schema = @Schema(implementation = InvoiceResponse.class)
            )
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "422",
            description = "Invoice rejected due to approval limit",
            content = @Content
    )
    @PostMapping
    public ResponseEntity<ApiResponse<InvoiceResponse>> createInvoice(@Valid @RequestBody InvoiceRequest invoiceRequest) {
        InvoiceResponse invoiceResponse = invoiceService.createInvoice(invoiceRequest);

        if (invoiceResponse.status() == InvoiceStatus.APPROVED) {
            return ResponseEntity.ok(ApiResponse.success("Invoice accepted", invoiceResponse));
        } else {
            return ResponseEntity.status(422).body(ApiResponse.error("Invoice rejected: limit exceeded"));
        }
    }

    @Operation(
            summary = "Get invoice by id",
            description = "Returns invoice details by invoice id"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Invoice found",
            content = @Content(
                    schema = @Schema(implementation = InvoiceResponse.class)
            )
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Invoice not found",
            content = @Content
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoiceById(@PathVariable String id) {
        InvoiceResponse invoiceResponse = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(ApiResponse.success("Invoice: ", invoiceResponse));
    }

    @Operation(
            summary = "List approved invoices",
            description = "Returns all approved invoices"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Approved invoices retrieved"
    )
    @GetMapping("/approved")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> getApprovedInvoices() {
        List<InvoiceResponse> list = invoiceService.getInvoicesByStatus(InvoiceStatus.APPROVED);
        return ResponseEntity.ok(ApiResponse.success("Approved invoices", list));
    }

    @Operation(
            summary = "List rejected invoices",
            description = "Returns all rejected invoices"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Rejected invoices retrieved"
    )
    @GetMapping("/rejected")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> getRejectedInvoices() {
        List<InvoiceResponse> list = invoiceService.getInvoicesByStatus(InvoiceStatus.REJECTED);
        return ResponseEntity.ok(ApiResponse.success("Rejected invoices", list));
    }

    @Operation(
            summary = "Cancel invoice",
            description = """
                    Cancels an invoice.

                    Rules:
                    - Only the invoice owner can cancel
                    - Approved invoices cannot be cancelled
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Invoice cancelled successfully"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "User is not the invoice owner",
            content = @Content
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "Approved invoices cannot be cancelled",
            content = @Content
    )
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelInvoice(@PathVariable Long id) {
        invoiceService.cancelInvoice(id);
        return ResponseEntity.ok(ApiResponse.success("Invoice cancelled", null));
    }
}
