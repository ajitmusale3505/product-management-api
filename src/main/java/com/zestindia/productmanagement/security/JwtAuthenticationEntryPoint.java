package com.zestindia.productmanagement.security;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.zestindia.productmanagement.exception.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import org.springframework.security.core.AuthenticationException;

import org.springframework.security.web.AuthenticationEntryPoint;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint
        implements AuthenticationEntryPoint {


    private final ObjectMapper objectMapper;


    @Override
    public void commence(

            HttpServletRequest request,

            HttpServletResponse response,

            AuthenticationException authenticationException

    ) throws IOException {


        ErrorResponse errorResponse =

                ErrorResponse.builder()

                        .timestamp(
                                LocalDateTime.now()
                        )

                        .status(
                                HttpStatus.UNAUTHORIZED.value()
                        )

                        .error(
                                HttpStatus.UNAUTHORIZED
                                        .getReasonPhrase()
                        )

                        .message(
                                "Authentication is required to access this resource"
                        )

                        .validationErrors(
                                null
                        )

                        .build();


        response.setStatus(
                HttpStatus.UNAUTHORIZED.value()
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