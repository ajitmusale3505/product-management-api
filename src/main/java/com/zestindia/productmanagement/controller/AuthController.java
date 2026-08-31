package com.zestindia.productmanagement.controller;

import com.zestindia.productmanagement.dto.request.LoginRequest;
import com.zestindia.productmanagement.dto.request.RefreshTokenRequest;
import com.zestindia.productmanagement.dto.request.RegisterRequest;
import com.zestindia.productmanagement.dto.response.AuthResponse;
import com.zestindia.productmanagement.service.AuthService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(

            @Valid
            @RequestBody RegisterRequest request

    ) {

        return ResponseEntity

                .status(HttpStatus.CREATED)

                .body(

                        authService.register(request)
                );
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(

            @Valid
            @RequestBody LoginRequest request

    ) {

        return ResponseEntity.ok(

                authService.login(request)
        );
    }


    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(

            @Valid
            @RequestBody RefreshTokenRequest request

    ) {

        return ResponseEntity.ok(

                authService.refresh(request)
        );
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(

            @Valid
            @RequestBody RefreshTokenRequest request

    ) {

        authService.logout(
                request.getRefreshToken()
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}