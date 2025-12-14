package com.emlakjet.purchaseinvoiceservice.service.impl;

import com.emlakjet.purchaseinvoiceservice.config.SecurityProperties;
import com.emlakjet.purchaseinvoiceservice.dto.response.NotificationResponse;
import com.emlakjet.purchaseinvoiceservice.model.entity.Invoice;
import com.emlakjet.purchaseinvoiceservice.model.entity.Notification;
import com.emlakjet.purchaseinvoiceservice.model.entity.Product;
import com.emlakjet.purchaseinvoiceservice.model.entity.User;
import com.emlakjet.purchaseinvoiceservice.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private SecurityProperties securityProperties;

    private Invoice invoice;

    @BeforeEach
    void setup() {

        when(securityProperties.getWebhookUrls())
                .thenReturn(List.of(
                        "http://webhook-1",
                        "http://webhook-2"
                ));

        notificationService = new NotificationServiceImpl(
                restTemplate,
                securityProperties,
                notificationRepository
        );

        User user = User.builder()
                .firstName("Halil Ibrahim")
                .lastName("Ozturk")
                .email("halilibrahim@ozturk.com")
                .build();

        Product product = Product.builder()
                .name("Laptop")
                .build();

        invoice = Invoice.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(100))
                .billNo("BILL-1")
                .product(product)
                .purchasingSpecialist(user)
                .build();
    }

    @Test
    void notifyInvoiceRejected_shouldSendWebhookCalls() {

        when(restTemplate.postForEntity(
                anyString(),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(ResponseEntity.ok("OK"));

        assertDoesNotThrow(() ->
                notificationService.notifyInvoiceRejected(invoice));

        verify(restTemplate, times(2))
                .postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void notifyInvoiceCancelled_shouldSendWebhookCalls() {

        when(restTemplate.postForEntity(
                anyString(),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(ResponseEntity.ok("OK"));

        notificationService.notifyInvoiceCancelled(invoice);

        verify(restTemplate, times(2))
                .postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void sendNotification_shouldNotFail_whenWebhookThrowsException() {

        when(restTemplate.postForEntity(
                anyString(),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new RuntimeException("Webhook down"));

        assertDoesNotThrow(() ->
                notificationService.notifyInvoiceRejected(invoice));
    }

    @Test
    void saveNotification_shouldSaveSuccessfully() {

        Notification notification = Notification.builder()
                .invoiceId("1")
                .email("halilibrahim@ozturk.com")
                .message("Test")
                .build();

        notificationService.saveNotification(notification);

        verify(notificationRepository).save(notification);
    }

    @Test
    void getAllNotifications_shouldReturnResponseList() {

        Notification notification = Notification.builder()
                .invoiceId("1")
                .firstName("Halil Ibrahim")
                .lastName("Ozturk")
                .email("halilibrahim@ozturk.com")
                .amount(BigDecimal.valueOf(100))
                .productName("Laptop")
                .billNo("BILL-1")
                .message("Rejected")
                .build();

        when(notificationRepository.findAll()).thenReturn(List.of(notification));

        List<NotificationResponse> responses =
                notificationService.getAllNotifications();

        assertEquals(1, responses.size());
        assertEquals("halilibrahim@ozturk.com", responses.get(0).email());
    }

}