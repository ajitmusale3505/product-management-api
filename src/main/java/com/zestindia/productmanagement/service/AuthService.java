package com.zestindia.productmanagement.service;

import com.zestindia.productmanagement.dto.request.LoginRequest;
import com.zestindia.productmanagement.dto.request.RefreshTokenRequest;
import com.zestindia.productmanagement.dto.request.RegisterRequest;
import com.zestindia.productmanagement.dto.response.AuthResponse;

import com.zestindia.productmanagement.entity.RefreshToken;
import com.zestindia.productmanagement.entity.User;

import com.zestindia.productmanagement.exception.InvalidRefreshTokenException;
import com.zestindia.productmanagement.exception.ResourceNotFoundException;
import com.zestindia.productmanagement.exception.UsernameAlreadyExistsException;

import com.zestindia.productmanagement.repository.RefreshTokenRepository;
import com.zestindia.productmanagement.repository.UserRepository;

import com.zestindia.productmanagement.security.CustomUserDetailsService;
import com.zestindia.productmanagement.security.JwtService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final CustomUserDetailsService userDetailsService;


    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;


    /*
     * Register New User
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (
                userRepository.existsByUsername(
                        request.getUsername()
                )
        ) {

            throw new UsernameAlreadyExistsException(
                    "Username already exists"
            );
        }


        User user = User.builder()

                .username(
                        request.getUsername()
                )

                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )

                .role(
                        request.getRole()
                )

                .enabled(true)

                .build();


        User savedUser =
                userRepository.save(user);


        return generateAuthenticationResponse(
                savedUser
        );
    }


    /*
     * User Login
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(

                new UsernamePasswordAuthenticationToken(

                        request.getUsername(),

                        request.getPassword()
                )
        );


        User user = userRepository

                .findByUsername(
                        request.getUsername()
                )

                .orElseThrow(

                        () -> new ResourceNotFoundException(

                                "User not found with username: "

                                        + request.getUsername()
                        )
                );


        return generateAuthenticationResponse(
                user
        );
    }


    /*
     * Refresh Access Token
     *
     * Refresh Token Rotation
     */
    @Transactional
    public AuthResponse refresh(

            RefreshTokenRequest request

    ) {

        RefreshToken refreshToken =

                refreshTokenRepository

                        .findByToken(

                                request.getRefreshToken()
                        )

                        .orElseThrow(

                                () -> new InvalidRefreshTokenException(

                                        "Invalid refresh token"
                                )
                        );


        /*
         * Check if token is revoked
         */
        if (
                Boolean.TRUE.equals(
                        refreshToken.getRevoked()
                )
        ) {

            throw new InvalidRefreshTokenException(

                    "Refresh token has been revoked"
            );
        }


        /*
         * Check if token is expired
         */
        if (

                refreshToken

                        .getExpiryDate()

                        .isBefore(
                                LocalDateTime.now()
                        )
        ) {

            refreshTokenRepository.delete(
                    refreshToken
            );


            throw new InvalidRefreshTokenException(

                    "Refresh token has expired"
            );
        }


        User user =
                refreshToken.getUser();


        /*
         * Refresh Token Rotation
         *
         * Generate a completely new token
         */
        refreshToken.setToken(

                UUID.randomUUID()
                        .toString()
        );


        refreshToken.setExpiryDate(

                LocalDateTime.now()
                        .plusDays(7)
        );


        refreshToken.setRevoked(
                false
        );


        RefreshToken updatedRefreshToken =

                refreshTokenRepository.save(
                        refreshToken
                );


        /*
         * Generate New Access Token
         */
        UserDetails userDetails =

                userDetailsService

                        .loadUserByUsername(

                                user.getUsername()
                        );


        String accessToken =

                jwtService

                        .generateAccessToken(
                                userDetails
                        );


        return AuthResponse.builder()

                .accessToken(
                        accessToken
                )

                .refreshToken(
                        updatedRefreshToken.getToken()
                )

                .tokenType(
                        "Bearer"
                )

                .accessTokenExpiresIn(
                        accessTokenExpiration
                )

                .build();
    }


    /*
     * Logout User
     *
     * Revoke Refresh Token
     */
    @Transactional
    public void logout(

            String refreshTokenValue

    ) {

        refreshTokenRepository

                .findByToken(
                        refreshTokenValue
                )

                .ifPresent(

                        token -> {

                            token.setRevoked(
                                    true
                            );


                            refreshTokenRepository.save(
                                    token
                            );
                        }
                );
    }


    /*
     * Generate Authentication Response
     */
    private AuthResponse generateAuthenticationResponse(

            User user

    ) {

        UserDetails userDetails =

                userDetailsService

                        .loadUserByUsername(

                                user.getUsername()
                        );


        String accessToken =

                jwtService

                        .generateAccessToken(
                                userDetails
                        );


        RefreshToken refreshToken =

                createOrUpdateRefreshToken(
                        user
                );


        return AuthResponse.builder()

                .accessToken(
                        accessToken
                )

                .refreshToken(
                        refreshToken.getToken()
                )

                .tokenType(
                        "Bearer"
                )

                .accessTokenExpiresIn(
                        accessTokenExpiration
                )

                .build();
    }


    /*
     * Create or Update Refresh Token
     */
    private RefreshToken createOrUpdateRefreshToken(

            User user

    ) {

        return refreshTokenRepository

                .findByUser(user)

                .map(

                        existingToken -> {


                            existingToken.setToken(

                                    UUID.randomUUID()
                                            .toString()
                            );


                            existingToken.setExpiryDate(

                                    LocalDateTime.now()
                                            .plusDays(7)
                            );


                            existingToken.setRevoked(
                                    false
                            );


                            return refreshTokenRepository.save(

                                    existingToken
                            );
                        }
                )

                .orElseGet(

                        () -> {


                            RefreshToken refreshToken =

                                    RefreshToken.builder()

                                            .user(user)

                                            .token(

                                                    UUID.randomUUID()

                                                            .toString()
                                            )

                                            .expiryDate(

                                                    LocalDateTime.now()

                                                            .plusDays(7)
                                            )

                                            .revoked(false)

                                            .build();


                            return refreshTokenRepository.save(

                                    refreshToken
                            );
                        }
                );
    }
}