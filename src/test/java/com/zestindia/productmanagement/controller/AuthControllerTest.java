package com.zestindia.productmanagement.controller;

import com.zestindia.productmanagement.dto.request.LoginRequest;
import com.zestindia.productmanagement.dto.request.RefreshTokenRequest;
import com.zestindia.productmanagement.dto.request.RegisterRequest;
import com.zestindia.productmanagement.dto.response.AuthResponse;
import com.zestindia.productmanagement.service.AuthService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AuthControllerTest {


    @Mock
    private AuthService authService;


    @InjectMocks
    private AuthController authController;


    /*
     * =====================================
     * REGISTER TEST
     * =====================================
     */


    @Test
    void shouldRegisterUserSuccessfully() {


        RegisterRequest request =

                new RegisterRequest();


        AuthResponse authResponse =

                mock(

                        AuthResponse.class

                );


        when(

                authService.register(

                        request

                )

        )

                .thenReturn(

                        authResponse

                );


        ResponseEntity<AuthResponse> response =

                authController.register(

                        request

                );


        assertNotNull(

                response

        );


        assertEquals(

                HttpStatus.CREATED,

                response.getStatusCode()

        );


        assertEquals(

                authResponse,

                response.getBody()

        );


        verify(

                authService

        )

                .register(

                        request

                );
    }


    /*
     * =====================================
     * LOGIN TEST
     * =====================================
     */


    @Test
    void shouldLoginSuccessfully() {


        LoginRequest request =

                mock(

                        LoginRequest.class

                );


        AuthResponse authResponse =

                mock(

                        AuthResponse.class

                );


        when(

                authService.login(

                        request

                )

        )

                .thenReturn(

                        authResponse

                );


        ResponseEntity<AuthResponse> response =

                authController.login(

                        request

                );


        assertNotNull(

                response

        );


        assertEquals(

                HttpStatus.OK,

                response.getStatusCode()

        );


        assertEquals(

                authResponse,

                response.getBody()

        );


        verify(

                authService

        )

                .login(

                        request

                );
    }


    /*
     * =====================================
     * REFRESH TOKEN TEST
     * =====================================
     */


    @Test
    void shouldRefreshTokenSuccessfully() {


        RefreshTokenRequest request =

                mock(

                        RefreshTokenRequest.class

                );


        AuthResponse authResponse =

                mock(

                        AuthResponse.class

                );


        when(

                authService.refresh(

                        request

                )

        )

                .thenReturn(

                        authResponse

                );


        ResponseEntity<AuthResponse> response =

                authController.refresh(

                        request

                );


        assertNotNull(

                response

        );


        assertEquals(

                HttpStatus.OK,

                response.getStatusCode()

        );


        assertEquals(

                authResponse,

                response.getBody()

        );


        verify(

                authService

        )

                .refresh(

                        request

                );
    }


    /*
     * =====================================
     * LOGOUT TEST
     * =====================================
     */


    @Test
    void shouldLogoutSuccessfully() {


        RefreshTokenRequest request =

                mock(

                        RefreshTokenRequest.class

                );


        String refreshToken =

                "test-refresh-token";


        when(

                request.getRefreshToken()

        )

                .thenReturn(

                        refreshToken

                );


        ResponseEntity<Void> response =

                authController.logout(

                        request

                );


        assertNotNull(

                response

        );


        assertEquals(

                HttpStatus.NO_CONTENT,

                response.getStatusCode()

        );


        verify(

                authService

        )

                .logout(

                        refreshToken

                );
    }
}