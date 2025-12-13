package com.emlakjet.purchaseinvoiceservice.exception;

public class DuplicateBillNoException extends BusinessException {

    public DuplicateBillNoException(String billNo) {
        super("Invoice already exists with billNo: " + billNo);
    }
}