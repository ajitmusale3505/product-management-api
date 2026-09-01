package com.zestindia.productmanagement.integration;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.zestindia.productmanagement.dto.request.ItemRequest;
import com.zestindia.productmanagement.dto.request.ProductRequest;

import com.zestindia.productmanagement.entity.User;

import com.zestindia.productmanagement.enums.Role;

import com.zestindia.productmanagement.repository.ProductRepository;
import com.zestindia.productmanagement.repository.UserRepository;

import com.zestindia.productmanagement.security.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.test.context.ActiveProfiles;

import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductApiIntegrationTest {


    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private UserRepository userRepository;


    @Autowired
    private ProductRepository productRepository;


    @Autowired
    private JwtService jwtService;


    private String adminToken;

    private String userToken;


    @BeforeEach
    void setUp() {


        /*
         * Clean database
         */

        productRepository.deleteAll();

        userRepository.deleteAll();


        /*
         * Create ADMIN user
         */

        User admin = User.builder()

                .username("admin")

                .password("password")

                .role(Role.ADMIN)

                .enabled(true)

                .build();


        userRepository.save(admin);


        /*
         * Create USER
         */

        User user = User.builder()

                .username("user")

                .password("password")

                .role(Role.USER)

                .enabled(true)

                .build();


        userRepository.save(user);


        /*
         * Generate ADMIN JWT
         */

        UserDetails adminUserDetails =

                new org.springframework.security.core.userdetails.User(

                        "admin",

                        "password",

                        List.of(

                                new SimpleGrantedAuthority(

                                        "ROLE_ADMIN"

                                )

                        )

                );


        adminToken =

                jwtService.generateAccessToken(

                        adminUserDetails

                );


        /*
         * Generate USER JWT
         */

        UserDetails normalUserDetails =

                new org.springframework.security.core.userdetails.User(

                        "user",

                        "password",

                        List.of(

                                new SimpleGrantedAuthority(

                                        "ROLE_USER"

                                )

                        )

                );


        userToken =

                jwtService.generateAccessToken(

                        normalUserDetails

                );

    }


    /*
     * =====================================
     * CREATE PRODUCT
     * ADMIN SUCCESS
     * =====================================
     */

    @Test
    void shouldCreateProductSuccessfullyAsAdmin()

            throws Exception {


        ProductRequest request =

                ProductRequest.builder()

                        .productName(

                                "Laptop"

                        )

                        .items(

                                List.of(

                                        ItemRequest.builder()

                                                .quantity(10)

                                                .build(),

                                        ItemRequest.builder()

                                                .quantity(20)

                                                .build()

                                )

                        )

                        .build();


        mockMvc.perform(

                        post(

                                "/api/v1/products"

                        )

                                .header(

                                        "Authorization",

                                        "Bearer " + adminToken

                                )

                                .contentType(

                                        MediaType.APPLICATION_JSON

                                )

                                .content(

                                        objectMapper.writeValueAsString(

                                                request

                                        )

                                )

                )

                .andExpect(

                        status().isCreated()

                )

                .andExpect(

                        jsonPath(

                                "$.id"

                        ).exists()

                )

                .andExpect(

                        jsonPath(

                                "$.productName"

                        ).value(

                                "Laptop"

                        )

                )

                .andExpect(

                        jsonPath(

                                "$.createdBy"

                        ).value(

                                "admin"

                        )

                )

                .andExpect(

                        jsonPath(

                                "$.items.length()"

                        ).value(

                                2

                        )

                );

    }


    /*
     * =====================================
     * CREATE PRODUCT
     * USER SHOULD BE FORBIDDEN
     * =====================================
     */

    @Test
    void shouldNotAllowUserToCreateProduct()

            throws Exception {


        ProductRequest request =

                ProductRequest.builder()

                        .productName(

                                "Laptop"

                        )

                        .build();


        mockMvc.perform(

                        post(

                                "/api/v1/products"

                        )

                                .header(

                                        "Authorization",

                                        "Bearer " + userToken

                                )

                                .contentType(

                                        MediaType.APPLICATION_JSON

                                )

                                .content(

                                        objectMapper.writeValueAsString(

                                                request

                                        )

                                )

                )

                .andExpect(

                        status().isForbidden()

                );

    }


    /*
     * =====================================
     * CREATE PRODUCT
     * WITHOUT TOKEN
     * =====================================
     */

    @Test
    void shouldNotCreateProductWithoutAuthentication()

            throws Exception {


        ProductRequest request =

                ProductRequest.builder()

                        .productName(

                                "Laptop"

                        )

                        .build();


        mockMvc.perform(

                        post(

                                "/api/v1/products"

                        )

                                .contentType(

                                        MediaType.APPLICATION_JSON

                                )

                                .content(

                                        objectMapper.writeValueAsString(

                                                request

                                        )

                                )

                )

                .andExpect(

                        status().isUnauthorized()

                );

    }


    /*
     * =====================================
     * GET ALL PRODUCTS
     * USER SUCCESS
     * =====================================
     */

    @Test
    void shouldAllowUserToGetAllProducts()

            throws Exception {


        mockMvc.perform(

                        get(

                                "/api/v1/products"

                        )

                                .header(

                                        "Authorization",

                                        "Bearer " + userToken

                                )

                )

                .andExpect(

                        status().isOk()

                );

    }


    /*
     * =====================================
     * INVALID JWT
     * =====================================
     */

    @Test
    void shouldReturnUnauthorizedForInvalidToken()

            throws Exception {


        mockMvc.perform(

                        get(

                                "/api/v1/products"

                        )

                                .header(

                                        "Authorization",

                                        "Bearer invalid-token"

                                )

                )

                .andExpect(

                        status().isUnauthorized()

                );

    }


    /*
     * =====================================
     * PRODUCT VALIDATION
     * =====================================
     */

    @Test
    void shouldReturnBadRequestForInvalidProduct()

            throws Exception {


        ProductRequest request =

                ProductRequest.builder()

                        .productName(

                                ""

                        )

                        .build();


        mockMvc.perform(

                        post(

                                "/api/v1/products"

                        )

                                .header(

                                        "Authorization",

                                        "Bearer " + adminToken

                                )

                                .contentType(

                                        MediaType.APPLICATION_JSON

                                )

                                .content(

                                        objectMapper.writeValueAsString(

                                                request

                                        )

                                )

                )

                .andExpect(

                        status().isBadRequest()

                )

                .andExpect(

                        jsonPath(

                                "$.message"

                        ).value(

                                "Validation failed"

                        )

                );

    }

}