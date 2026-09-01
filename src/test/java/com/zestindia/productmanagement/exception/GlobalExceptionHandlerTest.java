package com.zestindia.productmanagement.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.*;


class GlobalExceptionHandlerTest {


    private GlobalExceptionHandler globalExceptionHandler;


    @BeforeEach
    void setUp() {

        globalExceptionHandler =
                new GlobalExceptionHandler();
    }


    /*
     * =====================================
     * RESOURCE NOT FOUND
     * =====================================
     */


    @Test
    void shouldHandleResourceNotFoundException() {


        ResourceNotFoundException exception =

                new ResourceNotFoundException(

                        "Product not found with id: 1"

                );


        ResponseEntity<ErrorResponse> response =

                globalExceptionHandler
                        .handleResourceNotFoundException(
                                exception
                        );


        assertEquals(

                HttpStatus.NOT_FOUND,

                response.getStatusCode()

        );


        assertNotNull(
                response.getBody()
        );


        assertEquals(

                404,

                response.getBody()
                        .getStatus()

        );


        assertEquals(

                "Not Found",

                response.getBody()
                        .getError()

        );


        assertEquals(

                "Product not found with id: 1",

                response.getBody()
                        .getMessage()

        );
    }


    /*
     * =====================================
     * RESOURCE ALREADY EXISTS
     * =====================================
     */


    @Test
    void shouldHandleResourceAlreadyExistsException() {


        ResourceAlreadyExistsException exception =

                new ResourceAlreadyExistsException(

                        "Product already exists"

                );


        ResponseEntity<ErrorResponse> response =

                globalExceptionHandler
                        .handleResourceAlreadyExistsException(
                                exception
                        );


        assertEquals(

                HttpStatus.CONFLICT,

                response.getStatusCode()

        );


        assertEquals(

                409,

                response.getBody()
                        .getStatus()

        );


        assertEquals(

                "Product already exists",

                response.getBody()
                        .getMessage()

        );
    }


    /*
     * =====================================
     * USERNAME ALREADY EXISTS
     * =====================================
     */


    @Test
    void shouldHandleUsernameAlreadyExistsException() {


        UsernameAlreadyExistsException exception =

                new UsernameAlreadyExistsException(

                        "Username already exists"

                );


        ResponseEntity<ErrorResponse> response =

                globalExceptionHandler
                        .handleUsernameAlreadyExistsException(
                                exception
                        );


        assertEquals(

                HttpStatus.CONFLICT,

                response.getStatusCode()

        );


        assertEquals(

                409,

                response.getBody()
                        .getStatus()

        );


        assertEquals(

                "Username already exists",

                response.getBody()
                        .getMessage()

        );
    }


    /*
     * =====================================
     * INVALID REFRESH TOKEN
     * =====================================
     */


    @Test
    void shouldHandleInvalidRefreshTokenException() {


        InvalidRefreshTokenException exception =

                new InvalidRefreshTokenException(

                        "Invalid refresh token"

                );


        ResponseEntity<ErrorResponse> response =

                globalExceptionHandler
                        .handleInvalidRefreshTokenException(
                                exception
                        );


        assertEquals(

                HttpStatus.UNAUTHORIZED,

                response.getStatusCode()

        );


        assertEquals(

                401,

                response.getBody()
                        .getStatus()

        );


        assertEquals(

                "Invalid refresh token",

                response.getBody()
                        .getMessage()

        );
    }


    /*
     * =====================================
     * INVALID ACCESS TOKEN
     * =====================================
     */


    @Test
    void shouldHandleInvalidTokenException() {


        InvalidTokenException exception =

                new InvalidTokenException(

                        "Invalid access token"

                );


        ResponseEntity<ErrorResponse> response =

                globalExceptionHandler
                        .handleInvalidTokenException(
                                exception
                        );


        assertEquals(

                HttpStatus.UNAUTHORIZED,

                response.getStatusCode()

        );


        assertEquals(

                401,

                response.getBody()
                        .getStatus()

        );


        assertEquals(

                "Invalid access token",

                response.getBody()
                        .getMessage()

        );
    }


    /*
     * =====================================
     * TOKEN EXPIRED
     * =====================================
     */


    @Test
    void shouldHandleTokenExpiredException() {


        TokenExpiredException exception =

                new TokenExpiredException(

                        "Token has expired"

                );


        ResponseEntity<ErrorResponse> response =

                globalExceptionHandler
                        .handleTokenExpiredException(
                                exception
                        );


        assertEquals(

                HttpStatus.UNAUTHORIZED,

                response.getStatusCode()

        );


        assertEquals(

                401,

                response.getBody()
                        .getStatus()

        );


        assertEquals(

                "Token has expired",

                response.getBody()
                        .getMessage()

        );
    }


    /*
     * =====================================
     * ILLEGAL ARGUMENT
     * =====================================
     */


    @Test
    void shouldHandleIllegalArgumentException() {


        IllegalArgumentException exception =

                new IllegalArgumentException(

                        "Minimum quantity cannot be greater than maximum quantity"

                );


        ResponseEntity<ErrorResponse> response =

                globalExceptionHandler
                        .handleIllegalArgumentException(
                                exception
                        );


        assertEquals(

                HttpStatus.BAD_REQUEST,

                response.getStatusCode()

        );


        assertEquals(

                400,

                response.getBody()
                        .getStatus()

        );


        assertEquals(

                "Minimum quantity cannot be greater than maximum quantity",

                response.getBody()
                        .getMessage()

        );
    }


    /*
     * =====================================
     * AUTHENTICATION FAILURE
     * =====================================
     */


    @Test
    void shouldHandleAuthenticationException() {


        AuthenticationException exception =

                new AuthenticationException(
                        "Authentication failed"
                ) {
                };


        ResponseEntity<ErrorResponse> response =

                globalExceptionHandler
                        .handleAuthenticationException(
                                exception
                        );


        assertEquals(

                HttpStatus.UNAUTHORIZED,

                response.getStatusCode()

        );


        assertEquals(

                401,

                response.getBody()
                        .getStatus()

        );


        assertEquals(

                "Invalid username or password",

                response.getBody()
                        .getMessage()

        );
    }


    /*
     * =====================================
     * ACCESS DENIED
     * =====================================
     */


    @Test
    void shouldHandleAccessDeniedException() {


        AccessDeniedException exception =

                new AccessDeniedException(

                        "Access denied"

                );


        ResponseEntity<ErrorResponse> response =

                globalExceptionHandler
                        .handleAccessDeniedException(
                                exception
                        );


        assertEquals(

                HttpStatus.FORBIDDEN,

                response.getStatusCode()

        );


        assertEquals(

                403,

                response.getBody()
                        .getStatus()

        );


        assertEquals(

                "You do not have permission to access this resource",

                response.getBody()
                        .getMessage()

        );
    }


    /*
     * =====================================
     * DATABASE CONSTRAINT VIOLATION
     * =====================================
     */


    @Test
    void shouldHandleDataIntegrityViolationException() {


        DataIntegrityViolationException exception =

                new DataIntegrityViolationException(

                        "Database constraint violation"

                );


        ResponseEntity<ErrorResponse> response =

                globalExceptionHandler
                        .handleDataIntegrityViolationException(
                                exception
                        );


        assertEquals(

                HttpStatus.CONFLICT,

                response.getStatusCode()

        );


        assertEquals(

                409,

                response.getBody()
                        .getStatus()

        );


        assertEquals(

                "Database operation violates a constraint",

                response.getBody()
                        .getMessage()

        );
    }


    /*
     * =====================================
     * RUNTIME EXCEPTION
     * =====================================
     */


    @Test
    void shouldHandleRuntimeException() {


        RuntimeException exception =

                new RuntimeException(

                        "Something is invalid"

                );


        ResponseEntity<ErrorResponse> response =

                globalExceptionHandler
                        .handleRuntimeException(
                                exception
                        );


        assertEquals(

                HttpStatus.BAD_REQUEST,

                response.getStatusCode()

        );


        assertEquals(

                400,

                response.getBody()
                        .getStatus()

        );


        assertEquals(

                "Something is invalid",

                response.getBody()
                        .getMessage()

        );
    }


    @Test
    void shouldHandleRuntimeExceptionWithNullMessage() {


        RuntimeException exception =

                new RuntimeException();


        ResponseEntity<ErrorResponse> response =

                globalExceptionHandler
                        .handleRuntimeException(
                                exception
                        );


        assertEquals(

                HttpStatus.BAD_REQUEST,

                response.getStatusCode()

        );


        assertEquals(

                "Invalid request",

                response.getBody()
                        .getMessage()

        );
    }


    /*
     * =====================================
     * GLOBAL EXCEPTION
     * =====================================
     */


    @Test
    void shouldHandleGlobalException() {


        Exception exception =

                new Exception(

                        "Unexpected error"

                );


        ResponseEntity<ErrorResponse> response =

                globalExceptionHandler
                        .handleGlobalException(
                                exception
                        );


        assertEquals(

                HttpStatus.INTERNAL_SERVER_ERROR,

                response.getStatusCode()

        );


        assertEquals(

                500,

                response.getBody()
                        .getStatus()

        );


        assertEquals(

                "Something went wrong",

                response.getBody()
                        .getMessage()

        );
    }
}