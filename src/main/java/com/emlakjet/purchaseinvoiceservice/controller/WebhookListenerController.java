package com.emlakjet.purchaseinvoiceservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/mock-webhook")
@Slf4j
public class WebhookListenerController {

    /**
     * Mock endpoint invoice rejected/cancel notification listener
     */
    @PostMapping
    public ResponseEntity<String> receiveWebhook(@RequestBody Map<String, Object> payload) {
        log.info("Webhook received payload: {}", payload);
        return ResponseEntity.ok("Received");
    }
}
