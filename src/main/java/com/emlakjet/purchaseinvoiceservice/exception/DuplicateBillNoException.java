package com.emlakjet.purchaseinvoiceservice.exception;

import org.springframework.http.HttpStatus;

public class DuplicateBillNoException extends BusinessException {

    public DuplicateBillNoException(String billNo) {
        super(
                "Invoice with bill number already exists: " + billNo,
                HttpStatus.CONFLICT,
                "DUPLICATE_BILL_NO"
        );
    }
}