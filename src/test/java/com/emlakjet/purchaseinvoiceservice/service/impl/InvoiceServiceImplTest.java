package com.emlakjet.purchaseinvoiceservice.service.impl;

import com.emlakjet.purchaseinvoiceservice.config.InvoiceApprovalProperties;
import com.emlakjet.purchaseinvoiceservice.dto.request.InvoiceRequest;
import com.emlakjet.purchaseinvoiceservice.dto.response.InvoiceResponse;
import com.emlakjet.purchaseinvoiceservice.exception.DuplicateBillNoException;
import com.emlakjet.purchaseinvoiceservice.exception.InvoiceCannotBeCancelledException;
import com.emlakjet.purchaseinvoiceservice.exception.InvoiceOwnershipException;
import com.emlakjet.purchaseinvoiceservice.exception.ProductNotFoundException;
import com.emlakjet.purchaseinvoiceservice.mapper.InvoiceMapper;
import com.emlakjet.purchaseinvoiceservice.model.InvoiceStatus;
import com.emlakjet.purchaseinvoiceservice.model.entity.Invoice;
import com.emlakjet.purchaseinvoiceservice.model.entity.Product;
import com.emlakjet.purchaseinvoiceservice.model.entity.User;
import com.emlakjet.purchaseinvoiceservice.repository.InvoiceRepository;
import com.emlakjet.purchaseinvoiceservice.repository.ProductRepository;
import com.emlakjet.purchaseinvoiceservice.repository.UserRepository;
import com.emlakjet.purchaseinvoiceservice.service.NotificationService;
import com.emlakjet.purchaseinvoiceservice.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    @Mock
    private InvoiceRepository invoiceRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private InvoiceMapper invoiceMapper;
    @Mock
    private NotificationService notificationService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private InvoiceApprovalProperties approvalProperties;

    private User user;
    private Product product;
    private InvoiceRequest request;
    private Invoice invoice;

    @BeforeEach
    void setup() {
        user = User.builder()
                .email("halilibrahim@ozturk.com")
                .firstName("Halil Ibrahim")
                .lastName("Ozturk")
                .build();

        product = Product.builder()
                .id(1L)
                .name("Laptop")
                .build();

        request = new InvoiceRequest(
                "Halil Ibrahim",
                "Ozturk",
                "halilibrahim@ozturk.com",
                BigDecimal.valueOf(100),
                "Laptop",
                "BILL-1"
        );

        invoice = Invoice.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(100))
                .billNo("BILL-1")
                .purchasingSpecialist(user)
                .product(product)
                .status(InvoiceStatus.APPROVED)
                .build();
    }

    @Test
    void createInvoice_shouldApprove_whenLimitNotExceeded() {

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUserEmail).thenReturn("halilibrahim@ozturk.com");

            when(userRepository.findByEmail("halilibrahim@ozturk.com")).thenReturn(Optional.of(user));
            when(invoiceRepository.existsByBillNoAndStatus("BILL-1", InvoiceStatus.APPROVED)).thenReturn(false);
            when(productRepository.findByName("Laptop")).thenReturn(Optional.of(product));
            when(invoiceRepository.sumApprovedAmountByEmail("halilibrahim@ozturk.com"))
                    .thenReturn(BigDecimal.ZERO);
            when(approvalProperties.getMaxLimit()).thenReturn(BigDecimal.valueOf(200));
            when(invoiceMapper.toEntity(request)).thenReturn(invoice);
            when(invoiceRepository.save(any())).thenReturn(invoice);
            when(invoiceMapper.toResponse(any())).thenReturn(
                    new InvoiceResponse("1", "Halil Ibrahim", "Ozturk", "halilibrahim@ozturk.com",
                            BigDecimal.valueOf(100), "Laptop", "BILL-1", InvoiceStatus.APPROVED)
            );

            InvoiceResponse response = invoiceService.createInvoice(request);

            assertEquals(InvoiceStatus.APPROVED, response.status());
            verify(notificationService, never()).notifyInvoiceRejected(any());
        }
    }

    @Test
    void createInvoice_shouldReject_whenLimitExceeded() {

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUserEmail).thenReturn("halilibrahim@ozturk.com");

            when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
            when(productRepository.findByName(any())).thenReturn(Optional.of(product));
            when(invoiceRepository.existsByBillNoAndStatus(any(), any())).thenReturn(false);
            when(invoiceRepository.sumApprovedAmountByEmail(any()))
                    .thenReturn(BigDecimal.valueOf(150));
            when(approvalProperties.getMaxLimit()).thenReturn(BigDecimal.valueOf(200));
            when(invoiceMapper.toEntity(request)).thenReturn(invoice);
            when(invoiceRepository.save(any())).thenReturn(invoice);

            invoiceService.createInvoice(request);

            verify(notificationService).notifyInvoiceRejected(any());
        }
    }

    @Test
    void createInvoice_shouldThrow_whenDuplicateBillNo() {

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUserEmail).thenReturn("halilibrahim@ozturk.com");

            when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
            when(invoiceRepository.existsByBillNoAndStatus(any(), eq(InvoiceStatus.APPROVED)))
                    .thenReturn(true);

            assertThrows(DuplicateBillNoException.class,
                    () -> invoiceService.createInvoice(request));
        }
    }

    @Test
    void createInvoice_shouldThrow_whenIdentityMismatch() {

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUserEmail).thenReturn("halilibrahim@ozturk.com");

            User another = User.builder()
                    .email("halilibrahim@ozturk.com")
                    .firstName("Jack")
                    .lastName("Ozturk")
                    .build();

            when(userRepository.findByEmail(any())).thenReturn(Optional.of(another));

            assertThrows(InvoiceOwnershipException.class,
                    () -> invoiceService.createInvoice(request));
        }
    }

    @Test
    void createInvoice_shouldThrow_whenProductNotFound() {

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUserEmail).thenReturn("halilibrahim@ozturk.com");

            when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
            when(invoiceRepository.existsByBillNoAndStatus(any(), any())).thenReturn(false);
            when(productRepository.findByName(any())).thenReturn(Optional.empty());

            assertThrows(ProductNotFoundException.class,
                    () -> invoiceService.createInvoice(request));
        }
    }

    @Test
    void cancelInvoice_shouldThrow_whenNotOwner() {

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUserEmail).thenReturn("other@mail.com");

            when(invoiceRepository.findById("1")).thenReturn(Optional.of(invoice));

            assertThrows(InvoiceOwnershipException.class,
                    () -> invoiceService.cancelInvoice(1L));
        }
    }

    @Test
    void cancelInvoice_shouldThrow_whenRejected() {

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUserEmail).thenReturn("halilibrahim@ozturk.com");

            invoice.setStatus(InvoiceStatus.REJECTED);

            when(invoiceRepository.findById("1")).thenReturn(Optional.of(invoice));

            assertThrows(InvoiceCannotBeCancelledException.class,
                    () -> invoiceService.cancelInvoice(1L));
        }
    }

    @Test
    void cancelInvoice_shouldCancelSuccessfully() {

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUserEmail).thenReturn("halilibrahim@ozturk.com");

            when(invoiceRepository.findById("1")).thenReturn(Optional.of(invoice));

            invoiceService.cancelInvoice(1L);

            assertEquals(InvoiceStatus.CANCELLED, invoice.getStatus());
            verify(notificationService).notifyInvoiceCancelled(invoice);
        }
    }

}
