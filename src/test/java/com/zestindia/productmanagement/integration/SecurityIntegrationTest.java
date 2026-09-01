package com.zestindia.productmanagement.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.zestindia.productmanagement.enums.Role;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
 
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

import org.springframework.test.context.ActiveProfiles;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request
        .MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result
        .MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {


    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;


    /*
     * ============================================================
     *
     *  1. ACCESS PROTECTED API WITHOUT JWT
     *
     * ============================================================
     */

    @Test
    void accessProtectedApiWithoutToken_ShouldReturnUnauthorized()
            throws Exception {

        mockMvc.perform(

                        get("/api/v1/products")

                )

                .andExpect(

                        status().isUnauthorized()

                );
    }


    /*
     * ============================================================
     *
     *  2. ACCESS PROTECTED API WITH INVALID JWT
     *
     * ============================================================
     */

    @Test
    void accessProtectedApiWithInvalidToken_ShouldReturnUnauthorized()
            throws Exception {

        mockMvc.perform(

                        get("/api/v1/products")

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
     * ============================================================
     *
     *  3. USER CAN VIEW PRODUCTS
     *
     * ============================================================
     */

    @Test
    void userShouldBeAbleToViewProducts()
            throws Exception {


        String accessToken =

                registerAndGetAccessToken(

                        "user_view",

                        Role.USER
                );


        mockMvc.perform(

                        get("/api/v1/products")

                                .header(

                                        "Authorization",

                                        "Bearer " + accessToken
                                )

                )

                .andExpect(

                        status().isOk()

                );
    }


    /*
     * ============================================================
     *
     *  4. USER CANNOT CREATE PRODUCT
     *
     * ============================================================
     */

    @Test
    void userShouldNotBeAbleToCreateProduct()
            throws Exception {


        String accessToken =

                registerAndGetAccessToken(

                        "user_create",

                        Role.USER
                );


        String requestBody = """

                {
                    "productName": "Laptop"
                }

                """;


        mockMvc.perform(

                        post("/api/v1/products")

                                .header(

                                        "Authorization",

                                        "Bearer " + accessToken
                                )

                                .contentType(

                                        MediaType.APPLICATION_JSON
                                )

                                .content(

                                        requestBody
                                )

                )

                .andExpect(

                        status().isForbidden()

                );
    }


    /*
     * ============================================================
     *
     *  5. USER CANNOT UPDATE PRODUCT
     *
     * ============================================================
     */

    @Test
    void userShouldNotBeAbleToUpdateProduct()
            throws Exception {


        String accessToken =

                registerAndGetAccessToken(

                        "user_update",

                        Role.USER
                );


        String requestBody = """

                {
                    "productName": "Updated Product"
                }

                """;


        mockMvc.perform(

                        put("/api/v1/products/1")

                                .header(

                                        "Authorization",

                                        "Bearer " + accessToken
                                )

                                .contentType(

                                        MediaType.APPLICATION_JSON
                                )

                                .content(

                                        requestBody
                                )

                )

                .andExpect(

                        status().isForbidden()

                );
    }


    /*
     * ============================================================
     *
     *  6. USER CANNOT DELETE PRODUCT
     *
     * ============================================================
     */

    @Test
    void userShouldNotBeAbleToDeleteProduct()
            throws Exception {


        String accessToken =

                registerAndGetAccessToken(

                        "user_delete",

                        Role.USER
                );


        mockMvc.perform(

                        delete("/api/v1/products/1")

                                .header(

                                        "Authorization",

                                        "Bearer " + accessToken
                                )

                )

                .andExpect(

                        status().isForbidden()

                );
    }


    /*
     * ============================================================
     *
     *  7. USER CANNOT CREATE ITEM
     *
     * ============================================================
     */

    @Test
    void userShouldNotBeAbleToCreateItem()
            throws Exception {


        String accessToken =

                registerAndGetAccessToken(

                        "user_item",

                        Role.USER
                );


        String requestBody = """

                {
                    "quantity": 10
                }

                """;


        mockMvc.perform(

                        post("/api/v1/products/1/items")

                                .header(

                                        "Authorization",

                                        "Bearer " + accessToken
                                )

                                .contentType(

                                        MediaType.APPLICATION_JSON
                                )

                                .content(

                                        requestBody
                                )

                )

                .andExpect(

                        status().isForbidden()

                );
    }


    /*
     * ============================================================
     *
     *  8. ADMIN CAN CREATE PRODUCT
     *
     * ============================================================
     */

    @Test
    void adminShouldBeAbleToCreateProduct()
            throws Exception {


        String accessToken =

                registerAndGetAccessToken(

                        "admin_create",

                        Role.ADMIN
                );


        String requestBody = """

                {
                    "productName": "Laptop"
                }

                """;


        mockMvc.perform(

                        post("/api/v1/products")

                                .header(

                                        "Authorization",

                                        "Bearer " + accessToken
                                )

                                .contentType(

                                        MediaType.APPLICATION_JSON
                                )

                                .content(

                                        requestBody
                                )

                )

                .andExpect(

                        status().isCreated()

                )

                .andExpect(

                        jsonPath(

                                "$.productName"

                        ).value(

                                "Laptop"
                        )

                );
    }


    /*
     * ============================================================
     *
     *  9. ADMIN CAN CREATE PRODUCT AND UPDATE IT
     *
     * ============================================================
     */

    @Test
    void adminShouldBeAbleToUpdateProduct()
            throws Exception {


        String accessToken =

                registerAndGetAccessToken(

                        "admin_update",

                        Role.ADMIN
                );


        Long productId =

                createProductAndGetId(

                        accessToken,

                        "Original Product"
                );


        String requestBody = """

                {
                    "productName": "Updated Product"
                }

                """;


        mockMvc.perform(

                        put(

                                "/api/v1/products/" + productId
                        )

                                .header(

                                        "Authorization",

                                        "Bearer " + accessToken
                                )

                                .contentType(

                                        MediaType.APPLICATION_JSON
                                )

                                .content(

                                        requestBody
                                )

                )

                .andExpect(

                        status().isOk()

                )

                .andExpect(

                        jsonPath(

                                "$.productName"

                        ).value(

                                "Updated Product"
                        )

                );
    }


    /*
     * ============================================================
     *
     *  10. ADMIN CAN DELETE PRODUCT
     *
     * ============================================================
     */

    @Test
    void adminShouldBeAbleToDeleteProduct()
            throws Exception {


        String accessToken =

                registerAndGetAccessToken(

                        "admin_delete",

                        Role.ADMIN
                );


        Long productId =

                createProductAndGetId(

                        accessToken,

                        "Product To Delete"
                );


        mockMvc.perform(

                        delete(

                                "/api/v1/products/" + productId
                        )

                                .header(

                                        "Authorization",

                                        "Bearer " + accessToken
                                )

                )

                .andExpect(

                        status().isNoContent()

                );
    }


    /*
     * ============================================================
     *
     *  11. ADMIN CAN CREATE ITEM
     *
     * ============================================================
     */

    @Test
    void adminShouldBeAbleToCreateItem()
            throws Exception {


        String accessToken =

                registerAndGetAccessToken(

                        "admin_item",

                        Role.ADMIN
                );


        Long productId =

                createProductAndGetId(

                        accessToken,

                        "Laptop"
                );


        String requestBody = """

                {
                    "quantity": 10
                }

                """;


        mockMvc.perform(

                        post(

                                "/api/v1/products/"

                                        + productId

                                        + "/items"
                        )

                                .header(

                                        "Authorization",

                                        "Bearer " + accessToken
                                )

                                .contentType(

                                        MediaType.APPLICATION_JSON
                                )

                                .content(

                                        requestBody
                                )

                )

                .andExpect(

                        status().isCreated()

                )

                .andExpect(

                        jsonPath(

                                "$.quantity"

                        ).value(

                                10
                        )

                );
    }


    /*
     * ============================================================
     *
     *  HELPER METHOD
     *
     *  REGISTER USER AND RETURN ACCESS TOKEN
     *
     * ============================================================
     */

    private String registerAndGetAccessToken(

            String username,

            Role role

    ) throws Exception {


        String requestBody =

                """

                {
                    "username": "%s",
                    "password": "password123",
                    "role": "%s"
                }

                """.formatted(

                        username,

                        role.name()
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

                                                requestBody
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
     * ============================================================
     *
     *  HELPER METHOD
     *
     *  CREATE PRODUCT AND RETURN PRODUCT ID
     *
     * ============================================================
     */

    private Long createProductAndGetId(

            String accessToken,

            String productName

    ) throws Exception {


        String requestBody =

                """

                {
                    "productName": "%s"
                }

                """.formatted(

                        productName
                );


        String response =

                mockMvc.perform(

                                post(

                                        "/api/v1/products"
                                )

                                        .header(

                                                "Authorization",

                                                "Bearer " + accessToken
                                        )

                                        .contentType(

                                                MediaType.APPLICATION_JSON
                                        )

                                        .content(

                                                requestBody
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