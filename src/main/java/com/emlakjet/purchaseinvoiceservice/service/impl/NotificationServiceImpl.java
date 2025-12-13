package com.emlakjet.purchaseinvoiceservice.service.impl;

import com.emlakjet.purchaseinvoiceservice.model.entity.Invoice;
import com.emlakjet.purchaseinvoiceservice.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void notifyRejectedInvoice(Invoice invoice) {
        //TODO: Will use Kafka, RabbitMQ vs. here...

        log.warn("SECURITY ALERT - Rejected Invoice | Email: {} | Amount: {} | BillNo: {}",
                invoice.getPurchasingSpecialist().getEmail(),
                invoice.getAmount(),
                invoice.getBillNo()
        );
    }
}
