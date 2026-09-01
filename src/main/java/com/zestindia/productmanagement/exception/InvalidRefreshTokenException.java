package com.zestindia.productmanagement.exception;

public class InvalidRefreshTokenException
        extends RuntimeException {

    public InvalidRefreshTokenException(
            String message
    ) {

        super(message);
    }
}	