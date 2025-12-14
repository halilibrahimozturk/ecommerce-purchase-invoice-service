package com.emlakjet.purchaseinvoiceservice.controller;

import com.emlakjet.purchaseinvoiceservice.dto.response.InvoiceResponse;
import com.emlakjet.purchaseinvoiceservice.service.InvoiceService;
import com.emlakjet.purchaseinvoiceservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/mock-webhook")
@Slf4j
@RequiredArgsConstructor
public class WebhookListenerController {

    private final NotificationService notificationService;

    private final InvoiceService invoiceService;

    /**
     * Mock endpoint invoice rejected/cancel notification listener
     */
    @PostMapping
    public ResponseEntity<String> receiveWebhook(@RequestBody Map<String, Object> payload) {
        try {
            log.info("Webhook payload received: {}", payload);

            String invoiceId = String.valueOf(payload.get("invoiceId"));
            String message = String.valueOf(payload.get("message"));

            InvoiceResponse invoiceResponse = invoiceService.getInvoiceById(invoiceId);

            notificationService.saveNotification(invoiceResponse, message);

            return ResponseEntity.ok("Received");
        } catch (Exception e) {
            log.error("Webhook processing failed", e);
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
