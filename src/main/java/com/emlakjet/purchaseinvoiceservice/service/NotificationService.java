package com.emlakjet.purchaseinvoiceservice.service;

import com.emlakjet.purchaseinvoiceservice.dto.response.NotificationResponse;
import com.emlakjet.purchaseinvoiceservice.model.entity.Invoice;
import com.emlakjet.purchaseinvoiceservice.model.entity.Notification;

import java.util.List;

/**
 * Service for managing invoice notifications.
 * Handles sending, saving, and retrieving notifications.
 */
public interface NotificationService {

    /**
     * Sends notification when an invoice is rejected.
     *
     * @param invoice rejected invoice
     */
    void notifyInvoiceRejected(Invoice invoice);

    /**
     * Sends notification when an invoice is cancelled.
     *
     * @param invoice cancelled invoice
     */
    void notifyInvoiceCancelled(Invoice invoice);

    /**
     * Saves a notification record to the database.
     *
     * @param notification notification entity
     */
    void saveNotification(Notification notification);

    /**
     * Retrieves all saved notifications.
     *
     * @return list of notification responses
     */
    List<NotificationResponse> getAllNotifications();
}
