package com.redhat.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class BusinessException extends Exception {
    private final String trxId;

    public BusinessException(String message, String trxId) {
        super(message);
        this.trxId = trxId;
    }

    public String getTrxId() {
        return trxId;
    }

    @Override
    public String toString() {
        return getMessage()+", trxId: "+trxId;
    }
}
