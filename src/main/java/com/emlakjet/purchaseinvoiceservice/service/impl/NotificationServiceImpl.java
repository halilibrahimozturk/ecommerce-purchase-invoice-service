package com.emlakjet.purchaseinvoiceservice.service.impl;

import com.emlakjet.purchaseinvoiceservice.config.SecurityProperties;
import com.emlakjet.purchaseinvoiceservice.dto.response.InvoiceResponse;
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

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final RestTemplate restTemplate;
    private final List<String> webhookUrls;

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(RestTemplate restTemplate, SecurityProperties securityProperties, NotificationRepository notificationRepository) {
        this.restTemplate = restTemplate;
        this.webhookUrls = securityProperties.getWebhookUrls();
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void notifyInvoiceRejected(Invoice invoice) {
        sendNotification(invoice, "Invoice rejected: limit exceeded");
    }

    @Override
    public void notifyInvoiceCancelled(Invoice invoice) {
        sendNotification(invoice, "Invoice cancelled by user");
    }

    public void sendNotification(Invoice invoice, String message) {
        try {
            log.warn("SECURITY ALERT - " + message + ". invoiceId={}, email={}, amount={}, billNo={}",
                    invoice.getId(), invoice.getPurchasingSpecialist().getEmail(), invoice.getAmount(), invoice.getBillNo());

            for (String url : webhookUrls) {
                try {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);

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

                    HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

                    restTemplate.postForEntity(url, request, String.class);
                } catch (Exception ex) {
                    log.error("Failed to send security notification to webhook {}: {}", url, ex.getMessage());
                }
            }

        } catch (Exception ex) {
            log.error("SecurityNotificationService unexpected error: {}", ex.getMessage());
        }
    }


    public void saveNotification(InvoiceResponse invoiceResponse, String message) {
        Notification notification = Notification.builder()
                .invoiceId(invoiceResponse.id())
                .firstName(invoiceResponse.firstName())
                .lastName(invoiceResponse.lastName())
                .email(invoiceResponse.email())
                .amount(invoiceResponse.amount())
                .productName(invoiceResponse.productName())
                .billNo(invoiceResponse.billNo())
                .message(message)
                .build();

        notificationRepository.save(notification);
        log.info("Notification saved: {}", notification);
    }

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