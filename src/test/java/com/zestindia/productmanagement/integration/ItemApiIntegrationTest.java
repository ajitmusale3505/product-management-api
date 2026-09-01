package com.zestindia.productmanagement.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.zestindia.productmanagement.dto.request.ItemRequest;
import com.zestindia.productmanagement.dto.request.ProductRequest;
import com.zestindia.productmanagement.dto.request.RegisterRequest;

import com.zestindia.productmanagement.entity.Item;
import com.zestindia.productmanagement.entity.Product;
import com.zestindia.productmanagement.enums.Role;

import com.zestindia.productmanagement.repository.ItemRepository;
import com.zestindia.productmanagement.repository.ProductRepository;
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

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request
        .MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result
        .MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class ItemApiIntegrationTest {


    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private UserRepository userRepository;


    @Autowired
    private RefreshTokenRepository refreshTokenRepository;


    @Autowired
    private ProductRepository productRepository;


    @Autowired
    private ItemRepository itemRepository;


    @BeforeEach
    void setUp() {

        /*
         * Delete child entities first
         */

        itemRepository.deleteAll();

        productRepository.deleteAll();

        refreshTokenRepository.deleteAll();

        userRepository.deleteAll();
    }


    /*
     * TEST 1
     *
     * ADMIN should create item successfully
     */
    @Test
    void createItem_AsAdmin_ShouldReturnCreated()
            throws Exception {


        String adminToken =
                registerAndGetAccessToken(
                        "itemadmin",
                        Role.ADMIN
                );


        Long productId =
                createProductAndGetId(
                        adminToken,
                        "Laptop"
                );


        ItemRequest request =
                ItemRequest.builder()

                        .quantity(10)

                        .build();


        mockMvc.perform(

                        post(
                                "/api/v1/products/"
                                        + productId
                                        + "/items"
                        )

                                .header(
                                        "Authorization",
                                        "Bearer " + adminToken
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
                        status().isCreated()
                )

                .andExpect(
                        jsonPath("$.id").exists()
                )

                .andExpect(
                        jsonPath("$.quantity")
                                .value(10)
                );


        Product product =
                productRepository
                        .findById(productId)
                        .orElseThrow();


        assertThat(
                itemRepository.findByProductId(productId)
        )

                .hasSize(1);


        assertThat(
                product.getProductName()
        )

                .isEqualTo("Laptop");
    }


    /*
     * TEST 2
     *
     * USER should NOT create an item
     */
    @Test
    void createItem_AsUser_ShouldReturnForbidden()
            throws Exception {


        String adminToken =
                registerAndGetAccessToken(
                        "productadmin",
                        Role.ADMIN
                );


        Long productId =
                createProductAndGetId(
                        adminToken,
                        "Mobile"
                );


        String userToken =
                registerAndGetAccessToken(
                        "normaluser",
                        Role.USER
                );


        ItemRequest request =
                ItemRequest.builder()

                        .quantity(5)

                        .build();


        mockMvc.perform(

                        post(
                                "/api/v1/products/"
                                        + productId
                                        + "/items"
                        )

                                .header(
                                        "Authorization",
                                        "Bearer " + userToken
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
                        status().isForbidden()
                );
    }


    /*
     * TEST 3
     *
     * USER should get all items
     * of an existing product
     */
    @Test
    void getItemsByProductId_AsUser_ShouldReturnItems()
            throws Exception {


        String adminToken =
                registerAndGetAccessToken(
                        "getitemsadmin",
                        Role.ADMIN
                );


        Long productId =
                createProductAndGetId(
                        adminToken,
                        "Keyboard"
                );


        /*
         * Create Items
         */

        ItemRequest itemRequest1 =
                ItemRequest.builder()
                        .quantity(10)
                        .build();


        ItemRequest itemRequest2 =
                ItemRequest.builder()
                        .quantity(20)
                        .build();


        mockMvc.perform(

                post(
                        "/api/v1/products/"
                                + productId
                                + "/items"
                )

                        .header(
                                "Authorization",
                                "Bearer " + adminToken
                        )

                        .contentType(
                                MediaType.APPLICATION_JSON
                        )

                        .content(
                                objectMapper
                                        .writeValueAsString(
                                                itemRequest1
                                        )
                        )
        );


        mockMvc.perform(

                post(
                        "/api/v1/products/"
                                + productId
                                + "/items"
                )

                        .header(
                                "Authorization",
                                "Bearer " + adminToken
                        )

                        .contentType(
                                MediaType.APPLICATION_JSON
                        )

                        .content(
                                objectMapper
                                        .writeValueAsString(
                                                itemRequest2
                                        )
                        )
        );


        String userToken =
                registerAndGetAccessToken(
                        "itemsviewer",
                        Role.USER
                );


        mockMvc.perform(

                        get(
                                "/api/v1/products/"
                                        + productId
                                        + "/items"
                        )

                                .header(
                                        "Authorization",
                                        "Bearer " + userToken
                                )
                )

                .andExpect(
                        status().isOk()
                )

                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )

                .andExpect(
                        jsonPath("$[0].quantity")
                                .exists()
                )

                .andExpect(
                        jsonPath("$[1].quantity")
                                .exists()
                );
    }


    /*
     * TEST 4
     *
     * Creating an item for a
     * non-existing product should fail
     */
    @Test
    void createItem_ForNonExistingProduct_ShouldReturnNotFound()
            throws Exception {


        String adminToken =
                registerAndGetAccessToken(
                        "notfoundadmin",
                        Role.ADMIN
                );


        ItemRequest request =
                ItemRequest.builder()

                        .quantity(10)

                        .build();


        mockMvc.perform(

                        post(
                                "/api/v1/products/99999/items"
                        )

                                .header(
                                        "Authorization",
                                        "Bearer " + adminToken
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
                        status().isNotFound()
                );
    }


    /*
     * TEST 5
     *
     * Getting items for a
     * non-existing product should fail
     */
    @Test
    void getItems_ForNonExistingProduct_ShouldReturnNotFound()
            throws Exception {


        String userToken =
                registerAndGetAccessToken(
                        "notfounduser",
                        Role.USER
                );


        mockMvc.perform(

                        get(
                                "/api/v1/products/99999/items"
                        )

                                .header(
                                        "Authorization",
                                        "Bearer " + userToken
                                )
                )

                .andExpect(
                        status().isNotFound()
                );
    }


    /*
     * TEST 6
     *
     * Creating item without JWT
     * should return Unauthorized
     */
    @Test
    void createItem_WithoutAuthentication_ShouldReturnUnauthorized()
            throws Exception {


        ItemRequest request =
                ItemRequest.builder()

                        .quantity(10)

                        .build();


        mockMvc.perform(

                        post(
                                "/api/v1/products/1/items"
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
                        status().isUnauthorized()
                );
    }


    /*
     * TEST 7
     *
     * Invalid quantity should fail validation
     */
    @Test
    void createItem_WithInvalidQuantity_ShouldReturnBadRequest()
            throws Exception {


        String adminToken =
                registerAndGetAccessToken(
                        "validationadmin",
                        Role.ADMIN
                );


        Long productId =
                createProductAndGetId(
                        adminToken,
                        "Mouse"
                );


        ItemRequest request =
                ItemRequest.builder()

                        .quantity(0)

                        .build();


        mockMvc.perform(

                        post(
                                "/api/v1/products/"
                                        + productId
                                        + "/items"
                        )

                                .header(
                                        "Authorization",
                                        "Bearer " + adminToken
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
                        status().isBadRequest()
                );
    }


    /*
     * Helper Method
     *
     * Register User and Return JWT
     */
    private String registerAndGetAccessToken(

            String username,

            Role role

    ) throws Exception {


        RegisterRequest request =
                new RegisterRequest();


        request.setUsername(
                username
        );


        request.setPassword(
                "password123"
        );


        request.setRole(
                role
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
                                                                request
                                                        )
                                        )
                        )

                        .andExpect(
                                status().isCreated()
                        )

                        .andReturn()

                        .getResponse()

                        .getContentAsString();


        JsonNode jsonNode =

                objectMapper.readTree(
                        response
                );


        return jsonNode

                .get(
                        "accessToken"
                )

                .asText();
    }


    /*
     * Helper Method
     *
     * Create Product and Return Product ID
     */
    private Long createProductAndGetId(

            String adminToken,

            String productName

    ) throws Exception {


        ProductRequest request =
                ProductRequest.builder()

                        .productName(
                                productName
                        )

                        .items(
                                new ArrayList<>()
                        )

                        .build();


        String response =

                mockMvc.perform(

                                post(
                                        "/api/v1/products"
                                )

                                        .header(

                                                "Authorization",

                                                "Bearer "
                                                        + adminToken
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
                                status().isCreated()
                        )

                        .andReturn()

                        .getResponse()

                        .getContentAsString();


        JsonNode jsonNode =

                objectMapper.readTree(
                        response
                );


        return jsonNode

                .get(
                        "id"
                )

                .asLong();
    }
}