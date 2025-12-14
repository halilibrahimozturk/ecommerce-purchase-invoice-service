package com.emlakjet.purchaseinvoiceservice.service;

import com.emlakjet.purchaseinvoiceservice.dto.response.InvoiceResponse;
import com.emlakjet.purchaseinvoiceservice.dto.response.NotificationResponse;
import com.emlakjet.purchaseinvoiceservice.model.entity.Invoice;

import java.util.List;

public interface NotificationService {

    void notifyInvoiceRejected(Invoice invoice);

    void notifyInvoiceCancelled(Invoice invoice);

    void saveNotification(InvoiceResponse invoiceResponse, String message);

    List<NotificationResponse> getAllNotifications();

}