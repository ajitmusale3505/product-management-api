package com.zestindia.productmanagement.service;

import com.zestindia.productmanagement.dto.request.LoginRequest;
import com.zestindia.productmanagement.dto.request.RefreshTokenRequest;
import com.zestindia.productmanagement.dto.request.RegisterRequest;
import com.zestindia.productmanagement.dto.response.AuthResponse;
import com.zestindia.productmanagement.entity.RefreshToken;
import com.zestindia.productmanagement.entity.User;
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


    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(true)
                .build();

        userRepository.save(user);

        return generateAuthenticationResponse(user);
    }


    @Transactional
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        return generateAuthenticationResponse(user);
    }


    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(request.getRefreshToken())
                .orElseThrow(() ->
                        new RuntimeException("Invalid refresh token")
                );

        if (Boolean.TRUE.equals(refreshToken.getRevoked())) {
            throw new RuntimeException("Refresh token has been revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {

            refreshTokenRepository.delete(refreshToken);

            throw new RuntimeException("Refresh token has expired");
        }

        User user = refreshToken.getUser();

        // Rotate the existing refresh token
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));
        refreshToken.setRevoked(false);

        RefreshToken updatedRefreshToken =
                refreshTokenRepository.save(refreshToken);

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        user.getUsername()
                );

        String accessToken =
                jwtService.generateAccessToken(userDetails);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(updatedRefreshToken.getToken())
                .tokenType("Bearer")
                .accessTokenExpiresIn(accessTokenExpiration)
                .build();
    }


    @Transactional
    public void logout(String refreshTokenValue) {

        refreshTokenRepository
                .findByToken(refreshTokenValue)
                .ifPresent(token -> {

                    token.setRevoked(true);

                    refreshTokenRepository.save(token);
                });
    }


    private AuthResponse generateAuthenticationResponse(User user) {

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        user.getUsername()
                );

        String accessToken =
                jwtService.generateAccessToken(userDetails);

        RefreshToken refreshToken =
                createOrUpdateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .accessTokenExpiresIn(accessTokenExpiration)
                .build();
    }


    private RefreshToken createOrUpdateRefreshToken(User user) {

        return refreshTokenRepository
                .findByUser(user)
                .map(existingToken -> {

                    existingToken.setToken(
                            UUID.randomUUID().toString()
                    );

                    existingToken.setExpiryDate(
                            LocalDateTime.now().plusDays(7)
                    );

                    existingToken.setRevoked(false);

                    return refreshTokenRepository.save(existingToken);

                })
                .orElseGet(() -> {

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

                    return refreshTokenRepository.save(refreshToken);
                });
    }
}