package com.emlakjet.purchaseinvoiceservice.service;

import com.emlakjet.purchaseinvoiceservice.model.entity.Invoice;

public interface NotificationService {

    void notifyRejectedInvoice(Invoice invoice);
}