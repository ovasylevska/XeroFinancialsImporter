package com.xerofinancials.importer.exceptions;

public class DoNotRollbackException extends RuntimeException {
    private Exception exception;

    public DoNotRollbackException(Exception e) {
        this.exception = e;
    }

    public Exception getException() {
        return exception;
    }
}
