package com.emlakjet.purchaseinvoiceservice.controller;

import com.emlakjet.purchaseinvoiceservice.dto.request.InvoiceRequest;
import com.emlakjet.purchaseinvoiceservice.dto.response.CommonApiResponse;
import com.emlakjet.purchaseinvoiceservice.dto.response.InvoiceResponse;
import com.emlakjet.purchaseinvoiceservice.model.InvoiceStatus;
import com.emlakjet.purchaseinvoiceservice.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
        description = """
                Invoice creation, retrieval, approval and cancellation operations.
                                
                Role-based access:
                - PURCHASING_SPECIALIST: Create, list own invoices, cancel own invoices
                - FINANCE_SPECIALIST: View all invoices by status
                """
)
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Operation(
            summary = "Create invoice",
            description = """
                    Creates a new invoice for the logged-in PURCHASING_SPECIALIST.
                                        
                    Business rules:
                    - Invoice must be created using the user's own identity information
                    - Bill number must be unique among APPROVED invoices
                    - If total approved amount exceeds the configured limit, invoice is REJECTED
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Invoice created (APPROVED or REJECTED)",
            content = @Content(
                    schema = @Schema(implementation = InvoiceResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Validation error",
            content = @Content
    )
    @ApiResponse(
            responseCode = "403",
            description = "Invoice ownership violation",
            content = @Content
    )
    @ApiResponse(
            responseCode = "404",
            description = "Product or user not found",
            content = @Content
    )
    @ApiResponse(
            responseCode = "409",
            description = "Duplicate bill number",
            content = @Content
    )
    @PostMapping
    public ResponseEntity<CommonApiResponse<InvoiceResponse>> createInvoice(@Valid @RequestBody InvoiceRequest invoiceRequest) {
        InvoiceResponse invoiceResponse = invoiceService.createInvoice(invoiceRequest);

        if (invoiceResponse.status() == InvoiceStatus.APPROVED) {
            return ResponseEntity.ok(CommonApiResponse.success("Invoice accepted", invoiceResponse));
        } else {
            return ResponseEntity.status(422).body(CommonApiResponse.error("Invoice rejected: limit exceeded"));
        }
    }

    @Operation(
            summary = "Get invoice by id",
            description = """
                    Returns invoice details by invoice id.
                                        
                    Access rules:
                    - PURCHASING_SPECIALIST: Can access own invoices
                    - FINANCE_SPECIALIST: Can access all invoices
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Invoice found",
            content = @Content(
                    schema = @Schema(implementation = InvoiceResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Invoice not found",
            content = @Content
    )
    @GetMapping("/{id}")
    public ResponseEntity<CommonApiResponse<InvoiceResponse>> getInvoiceById(@PathVariable String id) {
        InvoiceResponse invoiceResponse = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(CommonApiResponse.success("Invoice: ", invoiceResponse));
    }

    @Operation(
            summary = "List approved invoices",
            description = """
                    Returns all APPROVED invoices.
                                        
                    Access:
                    - FINANCE_SPECIALIST only
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Approved invoices retrieved"
    )
    @GetMapping("/approved")
    public ResponseEntity<CommonApiResponse<List<InvoiceResponse>>> getApprovedInvoices() {
        List<InvoiceResponse> list = invoiceService.getInvoicesByStatus(InvoiceStatus.APPROVED);
        return ResponseEntity.ok(CommonApiResponse.success("Approved invoices", list));
    }

    @Operation(
            summary = "List rejected invoices",
            description = """
                    Returns all REJECTED invoices.
                                        
                    Access:
                    - FINANCE_SPECIALIST only
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Rejected invoices retrieved"
    )
    @GetMapping("/rejected")
    public ResponseEntity<CommonApiResponse<List<InvoiceResponse>>> getRejectedInvoices() {
        List<InvoiceResponse> list = invoiceService.getInvoicesByStatus(InvoiceStatus.REJECTED);
        return ResponseEntity.ok(CommonApiResponse.success("Rejected invoices", list));
    }

    @Operation(
            summary = "Cancel invoice",
            description = """
                    Cancels an invoice.
                                        
                    Rules:
                    - Only the invoice owner can cancel
                    - REJECTED or CANCELLED invoices cannot be cancelled
                    - APPROVED invoices cannot be cancelled
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Invoice cancelled successfully"
    )
    @ApiResponse(
            responseCode = "403",
            description = "User is not the invoice owner",
            content = @Content
    )
    @ApiResponse(
            responseCode = "409",
            description = "Invoice cannot be cancelled in current status",
            content = @Content
    )
    @ApiResponse(
            responseCode = "404",
            description = "Invoice not found",
            content = @Content
    )
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<CommonApiResponse<Void>> cancelInvoice(@PathVariable Long id) {
        invoiceService.cancelInvoice(id);
        return ResponseEntity.ok(CommonApiResponse.success("Invoice cancelled", null));
    }
}
