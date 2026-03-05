package com.ozturk.purchaseinvoiceservice.service.rule;

import com.ozturk.purchaseinvoiceservice.config.InvoiceApprovalProperties;
import com.ozturk.purchaseinvoiceservice.model.InvoiceStatus;
import com.ozturk.purchaseinvoiceservice.model.entity.Invoice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InvoiceApprovalRuleEngine {

    private final InvoiceApprovalProperties properties;

    public InvoiceStatus evaluate(Invoice invoice) {

        if (invoice.getAmount().compareTo(properties.getMaxLimit()) > 0) {
            return InvoiceStatus.REJECTED;
        }

        // We can add new rules here in the future.

        return InvoiceStatus.APPROVED;
    }
}