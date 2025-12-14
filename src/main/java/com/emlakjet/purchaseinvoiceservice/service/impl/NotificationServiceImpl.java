package com.emlakjet.purchaseinvoiceservice.service.impl;

import com.emlakjet.purchaseinvoiceservice.config.SecurityProperties;
import com.emlakjet.purchaseinvoiceservice.dto.response.NotificationResponse;
import com.emlakjet.purchaseinvoiceservice.model.entity.Invoice;
import com.emlakjet.purchaseinvoiceservice.model.entity.Notification;
import com.emlakjet.purchaseinvoiceservice.repository.NotificationRepository;
import com.emlakjet.purchaseinvoiceservice.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Notification service implementation.
 *
 * Responsibilities:
 * - Publishes invoice-related events to external webhook endpoints
 * - Persists received webhook events via mock listener mechanism
 *
 * Architectural note:
 * In real-world usage, this would work in an event-driven manner
 * (e.g. Kafka / RabbitMQ / Webhook provider).
 * In this project, webhooks are mocked and incoming events are
 * stored in database for inspection.
 */
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final RestTemplate restTemplate;
    private final List<String> webhookUrls;
    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(
            RestTemplate restTemplate,
            SecurityProperties securityProperties,
            NotificationRepository notificationRepository
    ) {
        this.restTemplate = restTemplate;
        this.webhookUrls = securityProperties.getWebhookUrls();
        this.notificationRepository = notificationRepository;
    }

    /**
     * Sends notification when an invoice is rejected.
     */
    @Override
    public void notifyInvoiceRejected(Invoice invoice) {
        sendNotification(invoice, "Invoice rejected: limit exceeded");
    }

    /**
     * Sends notification when an invoice is cancelled.
     */
    @Override
    public void notifyInvoiceCancelled(Invoice invoice) {
        sendNotification(invoice, "Invoice cancelled by user");
    }

    /**
     * Publishes invoice event to all configured webhook URLs.
     *
     * Each webhook represents an external system (or mocked listener).
     * Failures in one webhook do not block others.
     */
    public void sendNotification(Invoice invoice, String message) {

        log.warn(
                "SECURITY ALERT - {} | invoiceId={}, email={}, amount={}, billNo={}",
                message,
                invoice.getId(),
                invoice.getPurchasingSpecialist().getEmail(),
                invoice.getAmount(),
                invoice.getBillNo()
        );

        for (String url : webhookUrls) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                // Event payload sent to webhook listener
                Map<String, Object> payload = Map.of(
                        "invoiceId", invoice.getId(),
                        "firstName", invoice.getPurchasingSpecialist().getFirstName(),
                        "lastName", invoice.getPurchasingSpecialist().getLastName(),
                        "email", invoice.getPurchasingSpecialist().getEmail(),
                        "amount", invoice.getAmount(),
                        "productName", invoice.getProduct().getName(),
                        "billNo", invoice.getBillNo(),
                        "message", message
                );

                restTemplate.postForEntity(
                        url,
                        new HttpEntity<>(payload, headers),
                        String.class
                );

            } catch (Exception ex) {
                // One webhook failure must not stop others
                log.error(
                        "Failed to send notification to webhook {}: {}",
                        url,
                        ex.getMessage()
                );
            }
        }
    }

    /**
     * Persists notification received by mock webhook listener.
     *
     * Used only for demonstration and inspection purposes.
     */
    public void saveNotification(Notification notification) {
        notificationRepository.save(notification);
        log.info("Notification saved for invoiceId={}", notification.getInvoiceId());
    }

    /**
     * Returns all stored notifications.
     *
     * These records represent webhook events received
     * by the mock listener.
     */
    @Override
    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAll()
                .stream()
                .map(n -> new NotificationResponse(
                        n.getInvoiceId(),
                        n.getFirstName(),
                        n.getLastName(),
                        n.getEmail(),
                        n.getAmount(),
                        n.getProductName(),
                        n.getBillNo(),
                        n.getMessage(),
                        n.getCreatedAt()
                ))
                .toList();
    }
}
