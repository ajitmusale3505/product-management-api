package com.zestindia.productmanagement.repository;

import com.zestindia.productmanagement.entity.RefreshToken;
import com.zestindia.productmanagement.entity.User;
import com.zestindia.productmanagement.enums.Role;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RefreshTokenRepositoryTest {


    @Autowired
    private RefreshTokenRepository refreshTokenRepository;


    @Autowired
    private UserRepository userRepository;


    @Test
    void findByToken_ShouldReturnRefreshToken_WhenTokenExists() {

        User user = createAndSaveUser(
                "refresh-user-1"
        );

        RefreshToken refreshToken =
                createAndSaveRefreshToken(
                        "token-123",
                        user
                );


        RefreshToken foundToken =
                refreshTokenRepository
                        .findByToken("token-123")
                        .orElse(null);


        assertNotNull(foundToken);

        assertEquals(
                refreshToken.getId(),
                foundToken.getId()
        );

        assertEquals(
                "token-123",
                foundToken.getToken()
        );
    }


    @Test
    void findByToken_ShouldReturnEmpty_WhenTokenDoesNotExist() {

        boolean exists =
                refreshTokenRepository
                        .findByToken("invalid-token")
                        .isPresent();


        assertFalse(exists);
    }


    @Test
    void findByUser_ShouldReturnRefreshToken_WhenUserExists() {

        User user = createAndSaveUser(
                "refresh-user-2"
        );

        RefreshToken refreshToken =
                createAndSaveRefreshToken(
                        "token-456",
                        user
                );


        RefreshToken foundToken =
                refreshTokenRepository
                        .findByUser(user)
                        .orElse(null);


        assertNotNull(foundToken);

        assertEquals(
                refreshToken.getId(),
                foundToken.getId()
        );

        assertEquals(
                user.getId(),
                foundToken.getUser().getId()
        );
    }


    @Test
    void findByUser_ShouldReturnEmpty_WhenUserDoesNotHaveRefreshToken() {

        User user = createAndSaveUser(
                "refresh-user-3"
        );


        boolean exists =
                refreshTokenRepository
                        .findByUser(user)
                        .isPresent();


        assertFalse(exists);
    }


    @Test
    void deleteByUser_ShouldDeleteRefreshToken() {

        User user = createAndSaveUser(
                "refresh-user-4"
        );

        createAndSaveRefreshToken(
                "token-789",
                user
        );


        refreshTokenRepository.deleteByUser(user);


        boolean exists =
                refreshTokenRepository
                        .findByUser(user)
                        .isPresent();


        assertFalse(exists);
    }


    private User createAndSaveUser(
            String username
    ) {

        User user = User.builder()
                .username(username)
                .password("password123")
                .role(Role.USER)
                .enabled(true)
                .build();

        return userRepository.save(user);
    }


    private RefreshToken createAndSaveRefreshToken(

            String token,

            User user
    ) {

        RefreshToken refreshToken =
                RefreshToken.builder()
                        .token(token)
                        .user(user)
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
}