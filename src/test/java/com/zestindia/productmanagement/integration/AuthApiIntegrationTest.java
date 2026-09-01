package com.zestindia.productmanagement.integration;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.zestindia.productmanagement.dto.request.LoginRequest;
import com.zestindia.productmanagement.dto.request.RefreshTokenRequest;
import com.zestindia.productmanagement.dto.request.RegisterRequest;

import com.zestindia.productmanagement.entity.RefreshToken;
import com.zestindia.productmanagement.entity.User;

import com.zestindia.productmanagement.enums.Role;

import com.zestindia.productmanagement.repository.RefreshTokenRepository;
import com.zestindia.productmanagement.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
 

import org.springframework.boot.test.context
        .SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request
        .MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result
        .MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class AuthApiIntegrationTest {


    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private UserRepository userRepository;


    @Autowired
    private RefreshTokenRepository refreshTokenRepository;


    @BeforeEach
    void setUp() {

        refreshTokenRepository.deleteAll();

        userRepository.deleteAll();
    }


    /*
     * Test 1
     *
     * Register User Successfully
     */
    @Test
    void registerUser_ShouldReturnCreatedAndTokens()
            throws Exception {

        RegisterRequest request =
                new RegisterRequest();

        request.setUsername(
                "testuser"
        );

        request.setPassword(
                "password123"
        );

        request.setRole(
                Role.USER
        );


        mockMvc.perform(

                        post(
                                "/api/v1/auth/register"
                        )

                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )

                                .content(

                                        objectMapper

                                                .writeValueAsString(

                                                        request
                                                )
                                )
                )

                .andExpect(

                        status()
                                .isCreated()
                )

                .andExpect(

                        jsonPath(
                                "$.accessToken"
                        )

                                .exists()
                )

                .andExpect(

                        jsonPath(
                                "$.refreshToken"
                        )

                                .exists()
                )

                .andExpect(

                        jsonPath(
                                "$.tokenType"
                        )

                                .value(
                                        "Bearer"
                                )
                )


                .andExpect(

                        jsonPath(
                                "$.accessTokenExpiresIn"
                        )

                                .exists()
                );


        Optional<User> user =

                userRepository

                        .findByUsername(
                                "testuser"
                        );


        assertThat(
                user
        )

                .isPresent();


        assertThat(

                user.get()
                        .getRole()

        )

                .isEqualTo(
                        Role.USER
                );
    }


    /*
     * Test 2
     *
     * Duplicate Username
     */
    @Test
    void registerUser_WithDuplicateUsername_ShouldFail()
            throws Exception {


        RegisterRequest request =
                new RegisterRequest();

        request.setUsername(
                "duplicateuser"
        );

        request.setPassword(
                "password123"
        );

        request.setRole(
                Role.USER
        );


        /*
         * First Registration
         */

        mockMvc.perform(

                        post(
                                "/api/v1/auth/register"
                        )

                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )

                                .content(

                                        objectMapper

                                                .writeValueAsString(

                                                        request
                                                )
                                )
                )

                .andExpect(

                        status()
                                .isCreated()
                );


        /*
         * Second Registration
         */

        mockMvc.perform(

                        post(
                                "/api/v1/auth/register"
                        )

                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )

                                .content(

                                        objectMapper

                                                .writeValueAsString(

                                                        request
                                                )
                                )
                )

                .andExpect(

                        status()
                                .isConflict()
                );
    }


    /*
     * Test 3
     *
     * Login Successfully
     */
    @Test
    void loginUser_ShouldReturnTokens()
            throws Exception {


        /*
         * Register User First
         */

        RegisterRequest registerRequest =
                new RegisterRequest();

        registerRequest.setUsername(
                "loginuser"
        );

        registerRequest.setPassword(
                "password123"
        );

        registerRequest.setRole(
                Role.USER
        );


        mockMvc.perform(

                post(
                        "/api/v1/auth/register"
                )

                        .contentType(
                                MediaType.APPLICATION_JSON
                        )

                        .content(

                                objectMapper

                                        .writeValueAsString(

                                                registerRequest
                                        )
                        )
        );


        /*
         * Login Request
         */

        LoginRequest loginRequest =
                new LoginRequest();

        loginRequest.setUsername(
                "loginuser"
        );

        loginRequest.setPassword(
                "password123"
        );


        mockMvc.perform(

                        post(
                                "/api/v1/auth/login"
                        )

                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )

                                .content(

                                        objectMapper

                                                .writeValueAsString(

                                                        loginRequest
                                                )
                                )
                )

                .andExpect(

                        status()
                                .isOk()
                )

                .andExpect(

                        jsonPath(
                                "$.accessToken"
                        )

                                .exists()
                )

                .andExpect(

                        jsonPath(
                                "$.refreshToken"
                        )

                                .exists()
                )

                .andExpect(

                        jsonPath(
                                "$.tokenType"
                        )

                                .value(
                                        "Bearer"
                                )
                );
    }


    /*
     * Test 4
     *
     * Login With Invalid Password
     */
    @Test
    void loginUser_WithInvalidPassword_ShouldReturnUnauthorized()
            throws Exception {


        /*
         * Register User
         */

        RegisterRequest registerRequest =
                new RegisterRequest();

        registerRequest.setUsername(
                "invalidloginuser"
        );

        registerRequest.setPassword(
                "password123"
        );

        registerRequest.setRole(
                Role.USER
        );


        mockMvc.perform(

                post(
                        "/api/v1/auth/register"
                )

                        .contentType(
                                MediaType.APPLICATION_JSON
                        )

                        .content(

                                objectMapper

                                        .writeValueAsString(

                                                registerRequest
                                        )
                        )
        );


        /*
         * Invalid Login
         */

        LoginRequest loginRequest =
                new LoginRequest();

        loginRequest.setUsername(
                "invalidloginuser"
        );

        loginRequest.setPassword(
                "wrongpassword"
        );


        mockMvc.perform(

                        post(
                                "/api/v1/auth/login"
                        )

                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )

                                .content(

                                        objectMapper

                                                .writeValueAsString(

                                                        loginRequest
                                                )
                                )
                )

                .andExpect(

                        status()
                                .isUnauthorized()
                );
    }


    /*
     * Test 5
     *
     * Refresh Access Token
     */
    @Test
    void refreshToken_ShouldGenerateNewAccessToken()
            throws Exception {


        /*
         * Register User
         */

        RegisterRequest registerRequest =
                new RegisterRequest();

        registerRequest.setUsername(
                "refreshuser"
        );

        registerRequest.setPassword(
                "password123"
        );

        registerRequest.setRole(
                Role.USER
        );


        String response =

                mockMvc.perform(

                                post(
                                        "/api/v1/auth/register"
                                )

                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )

                                        .content(

                                                objectMapper

                                                        .writeValueAsString(

                                                                registerRequest
                                                        )
                                        )
                        )

                        .andExpect(

                                status()
                                        .isCreated()
                        )

                        .andReturn()

                        .getResponse()

                        .getContentAsString();


        String refreshToken =

                objectMapper

                        .readTree(
                                response
                        )

                        .get(
                                "refreshToken"
                        )

                        .asText();


        RefreshTokenRequest refreshRequest =
                new RefreshTokenRequest();

        refreshRequest.setRefreshToken(
                refreshToken
        );


        mockMvc.perform(

                        post(
                                "/api/v1/auth/refresh"
                        )

                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )

                                .content(

                                        objectMapper

                                                .writeValueAsString(

                                                        refreshRequest
                                                )
                                )
                )

                .andExpect(

                        status()
                                .isOk()
                )

                .andExpect(

                        jsonPath(
                                "$.accessToken"
                        )

                                .exists()
                )

                .andExpect(

                        jsonPath(
                                "$.refreshToken"
                        )

                                .exists()
                )

                .andExpect(

                        jsonPath(
                                "$.tokenType"
                        )

                                .value(
                                        "Bearer"
                                )
                );
    }


    /*
     * Test 6
     *
     * Logout User
     */
    @Test
    void logoutUser_ShouldRevokeRefreshToken()
            throws Exception {


        /*
         * Register User
         */

        RegisterRequest registerRequest =
                new RegisterRequest();

        registerRequest.setUsername(
                "logoutuser"
        );

        registerRequest.setPassword(
                "password123"
        );

        registerRequest.setRole(
                Role.USER
        );


        String response =

                mockMvc.perform(

                                post(
                                        "/api/v1/auth/register"
                                )

                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )

                                        .content(

                                                objectMapper

                                                        .writeValueAsString(

                                                                registerRequest
                                                        )
                                        )
                        )

                        .andExpect(

                                status()
                                        .isCreated()
                        )

                        .andReturn()

                        .getResponse()

                        .getContentAsString();


        String refreshTokenValue =

                objectMapper

                        .readTree(
                                response
                        )

                        .get(
                                "refreshToken"
                        )

                        .asText();


        RefreshTokenRequest logoutRequest =
                new RefreshTokenRequest();

        logoutRequest.setRefreshToken(
                refreshTokenValue
        );


        /*
         * Logout
         */

        mockMvc.perform(

                        post(
                                "/api/v1/auth/logout"
                        )

                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )

                                .content(

                                        objectMapper

                                                .writeValueAsString(

                                                        logoutRequest
                                                )
                                )
                )

                .andExpect(

                        status()
                                .isNoContent()
                );


        /*
         * Verify Refresh Token Is Revoked
         */

        RefreshToken refreshToken =

                refreshTokenRepository

                        .findByToken(
                                refreshTokenValue
                        )

                        .orElseThrow();


        assertThat(

                refreshToken.getRevoked()

        )

                .isTrue();
    }


    /*
     * Test 7
     *
     * Access Protected API
     * Without JWT
     */
    @Test
    void accessProtectedApi_WithoutToken_ShouldReturnUnauthorized()
            throws Exception {


        mockMvc.perform(

                        get(
                                "/api/v1/products"
                        )
                )

                .andExpect(

                        status()
                                .isUnauthorized()
                );
    }


    /*
     * Test 8
     *
     * Access Protected API
     * With Valid JWT
     */
    @Test
    void accessProtectedApi_WithValidToken_ShouldReturnOk()
            throws Exception {


        /*
         * Register User
         */

        RegisterRequest registerRequest =
                new RegisterRequest();

        registerRequest.setUsername(
                "protecteduser"
        );

        registerRequest.setPassword(
                "password123"
        );

        registerRequest.setRole(
                Role.USER
        );


        String response =

                mockMvc.perform(

                                post(
                                        "/api/v1/auth/register"
                                )

                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )

                                        .content(

                                                objectMapper

                                                        .writeValueAsString(

                                                                registerRequest
                                                        )
                                        )
                        )

                        .andExpect(

                                status()
                                        .isCreated()
                        )

                        .andReturn()

                        .getResponse()

                        .getContentAsString();


        String accessToken =

                objectMapper

                        .readTree(
                                response
                        )

                        .get(
                                "accessToken"
                        )

                        .asText();


        /*
         * Access Protected API
         */

        mockMvc.perform(

                        get(
                                "/api/v1/products"
                        )

                                .header(

                                        "Authorization",

                                        "Bearer "
                                                + accessToken
                                )
                )

                .andExpect(

                        status()
                                .isOk()
                );
    }
}