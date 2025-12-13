package com.emlakjet.purchaseinvoiceservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "invoice.approval")
public class InvoiceApprovalProperties {

    private BigDecimal maxLimit;
}