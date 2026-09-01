package com.zestindia.productmanagement.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zestindia.productmanagement.exception.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler
        implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;


    @Override
    public void handle(

            HttpServletRequest request,

            HttpServletResponse response,

            AccessDeniedException accessDeniedException

    ) throws IOException {

        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.FORBIDDEN.value())
                        .error(
                                HttpStatus.FORBIDDEN
                                        .getReasonPhrase()
                        )
                        .message(
                                "You do not have permission to access this resource"
                        )
                        .validationErrors(null)
                        .build();


        response.setStatus(
                HttpStatus.FORBIDDEN.value()
        );

        response.setContentType(
                "application/json"
        );

        objectMapper.writeValue(
                response.getOutputStream(),
                errorResponse
        );
    }
}