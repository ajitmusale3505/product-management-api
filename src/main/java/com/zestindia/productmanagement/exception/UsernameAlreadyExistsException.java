package com.zestindia.productmanagement.exception;

public class UsernameAlreadyExistsException
        extends RuntimeException {

    public UsernameAlreadyExistsException(
            String message
    ) {

        super(message);
    }
}