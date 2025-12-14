package com.emlakjet.purchaseinvoiceservice.controller;

import com.emlakjet.purchaseinvoiceservice.model.entity.Notification;
import com.emlakjet.purchaseinvoiceservice.service.InvoiceService;
import com.emlakjet.purchaseinvoiceservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/mock-webhook")
@Slf4j
@RequiredArgsConstructor
@Tag(
        name = "Mock Webhook Listener",
        description = """
                Mock webhook endpoint used to simulate event-driven communication.

                This controller represents an external system consuming webhook events.
                It exists purely for demonstration and testing purposes.
                """
)
public class WebhookListenerController {

    private final NotificationService notificationService;

    private final InvoiceService invoiceService;

    @Operation(
            summary = "Receive invoice notification event (mock webhook)",
            description = """
                    Receives invoice-related events via webhook calls.

                    Architectural overview:
                    - InvoiceService publishes events (invoice rejected / cancelled) via webhooks
                    - This controller acts as a mock webhook consumer
                    - Incoming events are persisted into the database as Notification entities
                    - Persisted events can later be viewed via NotificationController

                    This endpoint does NOT trigger business logic.
                    It only consumes and stores incoming events to demonstrate
                    an event-driven architecture.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Webhook event successfully received and stored"
    )
    @ApiResponse(
            responseCode = "500",
            description = "Failed to process webhook payload",
            content = @Content
    )
    @PostMapping
    public ResponseEntity<String> receiveWebhook(@RequestBody Map<String, Object> payload) {
        try {
            log.info("Webhook payload received: {}", payload);

            Notification notification = Notification.builder()
                    .invoiceId(String.valueOf(payload.get("invoiceId")))
                    .firstName(String.valueOf(payload.get("firstName")))
                    .lastName(String.valueOf(payload.get("lastName")))
                    .email(String.valueOf(payload.get("email")))
                    .amount(new BigDecimal(payload.get("amount").toString()))
                    .productName(String.valueOf(payload.get("productName")))
                    .billNo(String.valueOf(payload.get("billNo")))
                    .message(String.valueOf(payload.get("message")))
                    .build();

            notificationService.saveNotification(notification);

            return ResponseEntity.ok("Received");
        } catch (Exception e) {
            log.error("Webhook processing failed", e);
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
