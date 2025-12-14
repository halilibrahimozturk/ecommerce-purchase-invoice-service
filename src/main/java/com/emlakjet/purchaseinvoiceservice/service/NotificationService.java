package com.emlakjet.purchaseinvoiceservice.service;

import com.emlakjet.purchaseinvoiceservice.dto.response.NotificationResponse;
import com.emlakjet.purchaseinvoiceservice.model.entity.Invoice;
import com.emlakjet.purchaseinvoiceservice.model.entity.Notification;

import java.util.List;

public interface NotificationService {

    void notifyInvoiceRejected(Invoice invoice);

    void notifyInvoiceCancelled(Invoice invoice);

    void saveNotification(Notification notification);

    List<NotificationResponse> getAllNotifications();

}