package com.emlakjet.purchaseinvoiceservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    private List<String> webhookUrls;

    public List<String> getWebhookUrls() {
        return webhookUrls;
    }

    public void setWebhookUrls(List<String> webhookUrls) {
        this.webhookUrls = webhookUrls;
    }
}
