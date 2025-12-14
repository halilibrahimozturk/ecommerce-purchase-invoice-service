package com.emlakjet.purchaseinvoiceservice.service;

import com.emlakjet.purchaseinvoiceservice.model.entity.Invoice;

public interface NotificationService {

    void notifyInvoiceRejected(Invoice invoice);

    void notifyInvoiceCancelled(Invoice invoice);

}