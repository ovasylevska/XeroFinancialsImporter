package com.xerofinancials.importer.exceptions;

public class XeroAPIRateLimitException extends RuntimeException {

    public XeroAPIRateLimitException(String message) {
        super(message);
    }
}
