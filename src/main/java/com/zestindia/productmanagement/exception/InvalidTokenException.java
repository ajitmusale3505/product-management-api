package com.zestindia.productmanagement.exception;

public class InvalidTokenException
        extends RuntimeException {

    public InvalidTokenException(
            String message
    ) {
        super(message);
    }
}