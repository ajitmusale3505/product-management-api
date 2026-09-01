package com.zestindia.productmanagement.exception;

import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    /*
     * RESOURCE NOT FOUND
     */

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse>
    handleResourceNotFoundException(
            ResourceNotFoundException exception
    ) {

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                null
        );
    }


    /*
     * RESOURCE ALREADY EXISTS
     */

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse>
    handleResourceAlreadyExistsException(
            ResourceAlreadyExistsException exception
    ) {

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                null
        );
    }


    /*
     * USERNAME ALREADY EXISTS
     */

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse>
    handleUsernameAlreadyExistsException(
            UsernameAlreadyExistsException exception
    ) {

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                null
        );
    }


    /*
     * INVALID REFRESH TOKEN
     */

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponse>
    handleInvalidRefreshTokenException(
            InvalidRefreshTokenException exception
    ) {

        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage(),
                null
        );
    }


    /*
     * INVALID ACCESS TOKEN
     */

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse>
    handleInvalidTokenException(
            InvalidTokenException exception
    ) {

        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage(),
                null
        );
    }


    /*
     * TOKEN EXPIRED
     */

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponse>
    handleTokenExpiredException(
            TokenExpiredException exception
    ) {

        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage(),
                null
        );
    }


    /*
     * VALIDATION ERRORS
     */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse>
    handleValidationException(
            MethodArgumentNotValidException exception
    ) {

        Map<String, String> validationErrors =
                new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        validationErrors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                validationErrors
        );
    }


    /*
     * ILLEGAL ARGUMENT
     */

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse>
    handleIllegalArgumentException(
            IllegalArgumentException exception
    ) {

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                null
        );
    }


    /*
     * LOGIN AUTHENTICATION FAILURE
     */

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse>
    handleAuthenticationException(
            AuthenticationException exception
    ) {

        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Invalid username or password",
                null
        );
    }


    /*
     * ACCESS DENIED
     */

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse>
    handleAccessDeniedException(
            AccessDeniedException exception
    ) {

        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                "You do not have permission to access this resource",
                null
        );
    }


    /*
     * DATABASE CONSTRAINT VIOLATION
     */

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse>
    handleDataIntegrityViolationException(
            DataIntegrityViolationException exception
    ) {

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                "Database operation violates a constraint",
                null
        );
    }


    /*
     * OTHER RUNTIME EXCEPTIONS
     */

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse>
    handleRuntimeException(
            RuntimeException exception
    ) {

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage() != null
                        ? exception.getMessage()
                        : "Invalid request",
                null
        );
    }


    /*
     * GLOBAL EXCEPTION
     */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse>
    handleGlobalException(
            Exception exception
    ) {

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong",
                null
        );
    }


    /*
     * COMMON ERROR RESPONSE BUILDER
     */

    private ResponseEntity<ErrorResponse>
    buildErrorResponse(

            HttpStatus status,

            String message,

            Map<String, String> validationErrors

    ) {

        ErrorResponse errorResponse =

                ErrorResponse.builder()

                        .timestamp(
                                LocalDateTime.now()
                        )

                        .status(
                                status.value()
                        )

                        .error(
                                status.getReasonPhrase()
                        )

                        .message(
                                message
                        )

                        .validationErrors(
                                validationErrors
                        )

                        .build();


        return ResponseEntity

                .status(status)

                .body(errorResponse);
    }
}