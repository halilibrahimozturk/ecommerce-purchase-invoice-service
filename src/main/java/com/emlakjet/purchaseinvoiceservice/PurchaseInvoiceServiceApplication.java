package com.emlakjet.purchaseinvoiceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PurchaseInvoiceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PurchaseInvoiceServiceApplication.class, args);
	}

}
