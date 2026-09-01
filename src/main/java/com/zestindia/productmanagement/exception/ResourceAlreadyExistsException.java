package com.zestindia.productmanagement.exception;

public class ResourceAlreadyExistsException
        extends RuntimeException {

    public ResourceAlreadyExistsException(
            String message
    ) {
        super(message);
    }
}