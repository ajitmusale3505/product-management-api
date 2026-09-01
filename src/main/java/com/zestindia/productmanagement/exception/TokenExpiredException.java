package com.zestindia.productmanagement.exception;

public class TokenExpiredException
        extends RuntimeException {

    public TokenExpiredException(
            String message
    ) {
        super(message);
    }
}