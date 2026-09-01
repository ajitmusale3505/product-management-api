package com.zestindia.productmanagement.service;

import com.zestindia.productmanagement.dto.request.LoginRequest;
import com.zestindia.productmanagement.dto.request.RefreshTokenRequest;
import com.zestindia.productmanagement.dto.request.RegisterRequest;
import com.zestindia.productmanagement.dto.response.AuthResponse;

import com.zestindia.productmanagement.entity.RefreshToken;
import com.zestindia.productmanagement.entity.User;

import com.zestindia.productmanagement.enums.Role;

import com.zestindia.productmanagement.exception.InvalidRefreshTokenException;
import com.zestindia.productmanagement.exception.ResourceNotFoundException;
import com.zestindia.productmanagement.exception.UsernameAlreadyExistsException;

import com.zestindia.productmanagement.repository.RefreshTokenRepository;
import com.zestindia.productmanagement.repository.UserRepository;

import com.zestindia.productmanagement.security.CustomUserDetailsService;
import com.zestindia.productmanagement.security.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {


    @Mock
    private UserRepository userRepository;


    @Mock
    private RefreshTokenRepository refreshTokenRepository;


    @Mock
    private PasswordEncoder passwordEncoder;


    @Mock
    private AuthenticationManager authenticationManager;


    @Mock
    private JwtService jwtService;


    @Mock
    private CustomUserDetailsService userDetailsService;


    @Mock
    private UserDetails userDetails;


    @InjectMocks
    private AuthService authService;


    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(
                authService,
                "accessTokenExpiration",
                900000L
        );
    }


    /*
     * =====================================
     * REGISTER TESTS
     * =====================================
     */


    @Test
    void shouldRegisterUserSuccessfully() {


        RegisterRequest request =
                new RegisterRequest();


        request.setUsername(
                "ajit"
        );

        request.setPassword(
                "password123"
        );

        request.setRole(
                Role.USER
        );


        User savedUser =
                User.builder()

                        .id(1L)

                        .username("ajit")

                        .password("encodedPassword")

                        .role(Role.USER)

                        .enabled(true)

                        .build();


        RefreshToken refreshToken =
                RefreshToken.builder()

                        .id(1L)

                        .user(savedUser)

                        .token("refresh-token")

                        .expiryDate(
                                LocalDateTime.now()
                                        .plusDays(7)
                        )

                        .revoked(false)

                        .build();


        when(
                userRepository.existsByUsername(
                        "ajit"
                )
        )

                .thenReturn(
                        false
                );


        when(
                passwordEncoder.encode(
                        "password123"
                )
        )

                .thenReturn(
                        "encodedPassword"
                );


        when(
                userRepository.save(
                        any(User.class)
                )
        )

                .thenReturn(
                        savedUser
                );


        when(
                userDetailsService.loadUserByUsername(
                        "ajit"
                )
        )

                .thenReturn(
                        userDetails
                );


        when(
                jwtService.generateAccessToken(
                        userDetails
                )
        )

                .thenReturn(
                        "access-token"
                );


        when(
                refreshTokenRepository.findByUser(
                        savedUser
                )
        )

                .thenReturn(
                        Optional.empty()
                );


        when(
                refreshTokenRepository.save(
                        any(RefreshToken.class)
                )
        )

                .thenReturn(
                        refreshToken
                );


        AuthResponse response =
                authService.register(
                        request
                );


        assertNotNull(
                response
        );


        assertEquals(
                "access-token",
                response.getAccessToken()
        );


        assertEquals(
                "refresh-token",
                response.getRefreshToken()
        );


        assertEquals(
                "Bearer",
                response.getTokenType()
        );


        assertEquals(
                900000L,
                response.getAccessTokenExpiresIn()
        );


        verify(
                userRepository
        )

                .existsByUsername(
                        "ajit"
                );


        verify(
                passwordEncoder
        )

                .encode(
                        "password123"
                );


        verify(
                userRepository
        )

                .save(
                        any(User.class)
                );


        verify(
                jwtService
        )

                .generateAccessToken(
                        userDetails
                );
    }


    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {


        RegisterRequest request =
                new RegisterRequest();


        request.setUsername(
                "ajit"
        );

        request.setPassword(
                "password123"
        );

        request.setRole(
                Role.USER
        );


        when(
                userRepository.existsByUsername(
                        "ajit"
                )
        )

                .thenReturn(
                        true
                );


        UsernameAlreadyExistsException exception =

                assertThrows(

                        UsernameAlreadyExistsException.class,

                        () ->

                                authService.register(
                                        request
                                )
                );


        assertEquals(
                "Username already exists",
                exception.getMessage()
        );


        verify(
                userRepository,
                never()
        )

                .save(
                        any(User.class)
                );


        verifyNoInteractions(
                passwordEncoder,
                jwtService,
                userDetailsService,
                refreshTokenRepository
        );
    }


    @Test
    void shouldRegisterUserAndUpdateExistingRefreshToken() {


        RegisterRequest request =
                new RegisterRequest();


        request.setUsername(
                "ajit"
        );

        request.setPassword(
                "password123"
        );

        request.setRole(
                Role.USER
        );


        User savedUser =
                User.builder()

                        .id(1L)

                        .username("ajit")

                        .password("encodedPassword")

                        .role(Role.USER)

                        .enabled(true)

                        .build();


        RefreshToken existingRefreshToken =
                RefreshToken.builder()

                        .id(1L)

                        .user(savedUser)

                        .token("old-token")

                        .expiryDate(
                                LocalDateTime.now()
                                        .minusDays(1)
                        )

                        .revoked(true)

                        .build();


        when(
                userRepository.existsByUsername(
                        "ajit"
                )
        )

                .thenReturn(
                        false
                );


        when(
                passwordEncoder.encode(
                        "password123"
                )
        )

                .thenReturn(
                        "encodedPassword"
                );


        when(
                userRepository.save(
                        any(User.class)
                )
        )

                .thenReturn(
                        savedUser
                );


        when(
                userDetailsService.loadUserByUsername(
                        "ajit"
                )
        )

                .thenReturn(
                        userDetails
                );


        when(
                jwtService.generateAccessToken(
                        userDetails
                )
        )

                .thenReturn(
                        "access-token"
                );


        when(
                refreshTokenRepository.findByUser(
                        savedUser
                )
        )

                .thenReturn(
                        Optional.of(
                                existingRefreshToken
                        )
                );


        when(
                refreshTokenRepository.save(
                        existingRefreshToken
                )
        )

                .thenAnswer(
                        invocation ->
                                invocation.getArgument(0)
                );


        AuthResponse response =
                authService.register(
                        request
                );


        assertNotNull(
                response
        );


        assertEquals(
                "access-token",
                response.getAccessToken()
        );


        assertNotEquals(
                "old-token",
                response.getRefreshToken()
        );


        assertFalse(
                existingRefreshToken.getRevoked()
        );


        assertTrue(
                existingRefreshToken
                        .getExpiryDate()
                        .isAfter(
                                LocalDateTime.now()
                        )
        );


        verify(
                refreshTokenRepository
        )

                .save(
                        existingRefreshToken
                );
    }


    /*
     * =====================================
     * LOGIN TESTS
     * =====================================
     */


    @Test
    void shouldLoginSuccessfully() {


        LoginRequest request =
                new LoginRequest();


        request.setUsername(
                "ajit"
        );

        request.setPassword(
                "password123"
        );


        User user =
                User.builder()

                        .id(1L)

                        .username("ajit")

                        .password("encodedPassword")

                        .role(Role.USER)

                        .enabled(true)

                        .build();


        RefreshToken refreshToken =
                RefreshToken.builder()

                        .id(1L)

                        .user(user)

                        .token("refresh-token")

                        .expiryDate(
                                LocalDateTime.now()
                                        .plusDays(7)
                        )

                        .revoked(false)

                        .build();


        when(
                userRepository.findByUsername(
                        "ajit"
                )
        )

                .thenReturn(
                        Optional.of(
                                user
                        )
                );


        when(
                userDetailsService.loadUserByUsername(
                        "ajit"
                )
        )

                .thenReturn(
                        userDetails
                );


        when(
                jwtService.generateAccessToken(
                        userDetails
                )
        )

                .thenReturn(
                        "access-token"
                );


        when(
                refreshTokenRepository.findByUser(
                        user
                )
        )

                .thenReturn(
                        Optional.empty()
                );


        when(
                refreshTokenRepository.save(
                        any(RefreshToken.class)
                )
        )

                .thenReturn(
                        refreshToken
                );


        AuthResponse response =
                authService.login(
                        request
                );


        assertNotNull(
                response
        );


        assertEquals(
                "access-token",
                response.getAccessToken()
        );


        assertEquals(
                "refresh-token",
                response.getRefreshToken()
        );


        assertEquals(
                "Bearer",
                response.getTokenType()
        );


        verify(
                authenticationManager
        )

                .authenticate(
                        any(Authentication.class)
                );


        verify(
                userRepository
        )

                .findByUsername(
                        "ajit"
                );
    }


    @Test
    void shouldThrowExceptionWhenUserNotFoundDuringLogin() {


        LoginRequest request =
                new LoginRequest();


        request.setUsername(
                "unknown"
        );

        request.setPassword(
                "password123"
        );


        when(
                userRepository.findByUsername(
                        "unknown"
                )
        )

                .thenReturn(
                        Optional.empty()
                );


        ResourceNotFoundException exception =

                assertThrows(

                        ResourceNotFoundException.class,

                        () ->

                                authService.login(
                                        request
                                )
                );


        assertEquals(
                "User not found with username: unknown",
                exception.getMessage()
        );


        verify(
                authenticationManager
        )

                .authenticate(
                        any(Authentication.class)
                );


        verify(
                userRepository
        )

                .findByUsername(
                        "unknown"
                );


        verifyNoInteractions(
                jwtService,
                userDetailsService,
                refreshTokenRepository
        );
    }


    /*
     * =====================================
     * REFRESH TOKEN TESTS
     * =====================================
     */


    @Test
    void shouldRefreshAccessTokenSuccessfully() {


        User user =
                User.builder()

                        .id(1L)

                        .username("ajit")

                        .role(Role.USER)

                        .enabled(true)

                        .build();


        RefreshToken refreshToken =
                RefreshToken.builder()

                        .id(1L)

                        .user(user)

                        .token("old-refresh-token")

                        .expiryDate(
                                LocalDateTime.now()
                                        .plusDays(1)
                        )

                        .revoked(false)

                        .build();


        RefreshTokenRequest request =
                new RefreshTokenRequest();


        request.setRefreshToken(
                "old-refresh-token"
        );


        when(
                refreshTokenRepository.findByToken(
                        "old-refresh-token"
                )
        )

                .thenReturn(
                        Optional.of(
                                refreshToken
                        )
                );


        when(
                refreshTokenRepository.save(
                        refreshToken
                )
        )

                .thenAnswer(
                        invocation ->
                                invocation.getArgument(0)
                );


        when(
                userDetailsService.loadUserByUsername(
                        "ajit"
                )
        )

                .thenReturn(
                        userDetails
                );


        when(
                jwtService.generateAccessToken(
                        userDetails
                )
        )

                .thenReturn(
                        "new-access-token"
                );


        AuthResponse response =
                authService.refresh(
                        request
                );


        assertNotNull(
                response
        );


        assertEquals(
                "new-access-token",
                response.getAccessToken()
        );


        assertNotEquals(
                "old-refresh-token",
                response.getRefreshToken()
        );


        assertEquals(
                "Bearer",
                response.getTokenType()
        );


        assertEquals(
                900000L,
                response.getAccessTokenExpiresIn()
        );


        assertFalse(
                refreshToken.getRevoked()
        );


        verify(
                refreshTokenRepository
        )

                .save(
                        refreshToken
                );


        verify(
                jwtService
        )

                .generateAccessToken(
                        userDetails
                );
    }


    @Test
    void shouldThrowExceptionWhenRefreshTokenDoesNotExist() {


        RefreshTokenRequest request =
                new RefreshTokenRequest();


        request.setRefreshToken(
                "invalid-token"
        );


        when(
                refreshTokenRepository.findByToken(
                        "invalid-token"
                )
        )

                .thenReturn(
                        Optional.empty()
                );


        InvalidRefreshTokenException exception =

                assertThrows(

                        InvalidRefreshTokenException.class,

                        () ->

                                authService.refresh(
                                        request
                                )
                );


        assertEquals(
                "Invalid refresh token",
                exception.getMessage()
        );


        verify(
                refreshTokenRepository,
                never()
        )

                .save(
                        any(RefreshToken.class)
                );
    }


    @Test
    void shouldThrowExceptionWhenRefreshTokenIsRevoked() {


        RefreshToken refreshToken =
                RefreshToken.builder()

                        .token("revoked-token")

                        .expiryDate(
                                LocalDateTime.now()
                                        .plusDays(1)
                        )

                        .revoked(true)

                        .build();


        RefreshTokenRequest request =
                new RefreshTokenRequest();


        request.setRefreshToken(
                "revoked-token"
        );


        when(
                refreshTokenRepository.findByToken(
                        "revoked-token"
                )
        )

                .thenReturn(
                        Optional.of(
                                refreshToken
                        )
                );


        InvalidRefreshTokenException exception =

                assertThrows(

                        InvalidRefreshTokenException.class,

                        () ->

                                authService.refresh(
                                        request
                                )
                );


        assertEquals(
                "Refresh token has been revoked",
                exception.getMessage()
        );


        verify(
                refreshTokenRepository,
                never()
        )

                .save(
                        any(RefreshToken.class)
                );
    }


    @Test
    void shouldDeleteAndThrowExceptionWhenRefreshTokenIsExpired() {


        RefreshToken refreshToken =
                RefreshToken.builder()

                        .token("expired-token")

                        .expiryDate(
                                LocalDateTime.now()
                                        .minusDays(1)
                        )

                        .revoked(false)

                        .build();


        RefreshTokenRequest request =
                new RefreshTokenRequest();


        request.setRefreshToken(
                "expired-token"
        );


        when(
                refreshTokenRepository.findByToken(
                        "expired-token"
                )
        )

                .thenReturn(
                        Optional.of(
                                refreshToken
                        )
                );


        InvalidRefreshTokenException exception =

                assertThrows(

                        InvalidRefreshTokenException.class,

                        () ->

                                authService.refresh(
                                        request
                                )
                );


        assertEquals(
                "Refresh token has expired",
                exception.getMessage()
        );


        verify(
                refreshTokenRepository
        )

                .delete(
                        refreshToken
                );


        verify(
                refreshTokenRepository,
                never()
        )

                .save(
                        any(RefreshToken.class)
                );
    }


    /*
     * =====================================
     * LOGOUT TESTS
     * =====================================
     */


    @Test
    void shouldLogoutSuccessfully() {


        RefreshToken refreshToken =
                RefreshToken.builder()

                        .id(1L)

                        .token("refresh-token")

                        .expiryDate(
                                LocalDateTime.now()
                                        .plusDays(7)
                        )

                        .revoked(false)

                        .build();


        when(
                refreshTokenRepository.findByToken(
                        "refresh-token"
                )
        )

                .thenReturn(
                        Optional.of(
                                refreshToken
                        )
                );


        when(
                refreshTokenRepository.save(
                        refreshToken
                )
        )

                .thenReturn(
                        refreshToken
                );


        authService.logout(
                "refresh-token"
        );


        assertTrue(
                refreshToken.getRevoked()
        );


        verify(
                refreshTokenRepository
        )

                .save(
                        refreshToken
                );
    }


    @Test
    void shouldDoNothingWhenLogoutTokenDoesNotExist() {


        when(
                refreshTokenRepository.findByToken(
                        "invalid-token"
                )
        )

                .thenReturn(
                        Optional.empty()
                );


        authService.logout(
                "invalid-token"
        );


        verify(
                refreshTokenRepository,
                never()
        )

                .save(
                        any(RefreshToken.class)
                );
    }
}