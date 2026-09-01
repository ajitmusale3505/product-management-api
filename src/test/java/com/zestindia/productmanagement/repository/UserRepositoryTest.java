package com.zestindia.productmanagement.repository;

import com.zestindia.productmanagement.entity.User;
import com.zestindia.productmanagement.enums.Role;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)

class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;


    @Test
    void findByUsername_ShouldReturnUser_WhenUsernameExists() {

        User user = User.builder()
                .username("ajit")
                .password("password123")
                .role(Role.USER)
                .enabled(true)
                .build();

        userRepository.save(user);


        Optional<User> foundUser =
                userRepository.findByUsername("ajit");


        assertTrue(foundUser.isPresent());

        assertTrue(
                foundUser.get()
                        .getUsername()
                        .equals("ajit")
        );
    }


    @Test
    void findByUsername_ShouldReturnEmpty_WhenUsernameDoesNotExist() {

        Optional<User> foundUser =
                userRepository.findByUsername(
                        "unknown-user"
                );


        assertTrue(foundUser.isEmpty());
    }


    @Test
    void existsByUsername_ShouldReturnTrue_WhenUsernameExists() {

        User user = User.builder()
                .username("admin")
                .password("password123")
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        userRepository.save(user);


        boolean exists =
                userRepository.existsByUsername("admin");


        assertTrue(exists);
    }


    @Test
    void existsByUsername_ShouldReturnFalse_WhenUsernameDoesNotExist() {

        boolean exists =
                userRepository.existsByUsername(
                        "unknown-user"
                );


        assertFalse(exists);
    }
}