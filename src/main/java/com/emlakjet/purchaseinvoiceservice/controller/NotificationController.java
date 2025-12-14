package com.emlakjet.purchaseinvoiceservice.controller;

import com.emlakjet.purchaseinvoiceservice.dto.response.NotificationResponse;
import com.emlakjet.purchaseinvoiceservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(
        name = "Notifications",
        description = """
                Event-driven notification records.

                This controller does NOT trigger notifications.
                It only exposes stored notification events that were produced
                by webhook-based event publishing.
                """
)
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(
            summary = "List all notification events",
            description = """
                    Returns all notification records stored in the system.

                    Architectural note:
                    - Notifications are produced via webhook calls in an event-driven manner
                    - When an invoice event occurs (rejected / cancelled), a webhook is triggered
                    - For demo and testing purposes, a mock webhook listener persists incoming events into DB
                    - This endpoint simply exposes those persisted events for inspection

                    This endpoint exists to demonstrate event-driven behavior.
                    """
    )
    @GetMapping("/all")
    public ResponseEntity<List<NotificationResponse>> getAllNotifications() {
        List<NotificationResponse> list = notificationService.getAllNotifications();
        return ResponseEntity.ok(list);
    }
}