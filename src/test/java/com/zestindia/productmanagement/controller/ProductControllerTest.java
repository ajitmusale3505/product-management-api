package com.zestindia.productmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zestindia.productmanagement.dto.request.ItemRequest;
import com.zestindia.productmanagement.dto.request.ProductRequest;
import com.zestindia.productmanagement.dto.response.ItemResponse;
import com.zestindia.productmanagement.dto.response.ProductResponse;
import com.zestindia.productmanagement.exception.ResourceNotFoundException;
import com.zestindia.productmanagement.security.CustomUserDetailsService;
import com.zestindia.productmanagement.security.JwtAuthenticationEntryPoint;
import com.zestindia.productmanagement.security.JwtAuthenticationFilter;
import com.zestindia.productmanagement.service.ItemService;
import com.zestindia.productmanagement.service.ProductService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(com.zestindia.productmanagement.config.JacksonConfig.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private ItemService itemService;

    // Still required — SecurityConfig's SecurityFilterChain bean is
    // still built (its bean definition, not its runtime filter, is active),
    // so its constructor dependencies must still resolve.
    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    /*
     * CREATE PRODUCT
     */

    @Test
    void createProduct_ShouldReturnCreated() throws Exception {

        ProductRequest request =
                ProductRequest.builder()
                        .productName("Laptop")
                        .items(
                                List.of(
                                        ItemRequest.builder()
                                                .quantity(10)
                                                .build()
                                )
                        )
                        .build();


        ProductResponse response =
                ProductResponse.builder()
                        .id(1L)
                        .productName("Laptop")
                        .createdBy("admin")
                        .items(
                                List.of(
                                        ItemResponse.builder()
                                                .id(1L)
                                                .quantity(10)
                                                .build()
                                )
                        )
                        .build();


        when(
                productService.createProduct(
                        any(ProductRequest.class)
                )
        ).thenReturn(response);


        mockMvc.perform(
                        post("/api/v1/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(
                        jsonPath("$.productName").value("Laptop")
                );


        verify(productService)
                .createProduct(
                        any(ProductRequest.class)
                );
    }


    /*
     * CREATE PRODUCT VALIDATION
     */

    @Test
    void createProduct_WithBlankName_ShouldReturnBadRequest()
            throws Exception {

        ProductRequest request =
                ProductRequest.builder()
                        .productName("")
                        .build();


        mockMvc.perform(
                        post("/api/v1/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.message")
                                .value("Validation failed")
                )
                .andExpect(
                        jsonPath("$.validationErrors.productName")
                                .value("Product name is required")
                );


        verifyNoInteractions(productService);
    }


    /*
     * GET PRODUCT BY ID
     */

    @Test
    void getProductById_ShouldReturnProduct()
            throws Exception {

        ProductResponse response =
                ProductResponse.builder()
                        .id(1L)
                        .productName("Laptop")
                        .createdBy("admin")
                        .items(List.of())
                        .build();


        when(
                productService.getProductById(1L)
        ).thenReturn(response);


        mockMvc.perform(
                        get(
                                "/api/v1/products/{id}",
                                1L
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(
                        jsonPath("$.productName")
                                .value("Laptop")
                );


        verify(productService)
                .getProductById(1L);
    }


    /*
     * GET PRODUCT NOT FOUND
     */

    @Test
    void getProductById_WhenNotFound_ShouldReturn404()
            throws Exception {

        when(
                productService.getProductById(100L)
        ).thenThrow(
                new ResourceNotFoundException(
                        "Product not found with id: 100"
                )
        );


        mockMvc.perform(
                        get(
                                "/api/v1/products/{id}",
                                100L
                        )
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Product not found with id: 100"
                                )
                );


        verify(productService)
                .getProductById(100L);
    }


    /*
     * GET ALL PRODUCTS
     */

    @Test
    void getAllProducts_ShouldReturnProducts()
            throws Exception {

        ProductResponse product1 =
                ProductResponse.builder()
                        .id(1L)
                        .productName("Laptop")
                        .items(List.of())
                        .build();


        ProductResponse product2 =
                ProductResponse.builder()
                        .id(2L)
                        .productName("Mouse")
                        .items(List.of())
                        .build();


        Page<ProductResponse> productPage =
                new PageImpl<>(
                        List.of(
                                product1,
                                product2
                        ),
                        PageRequest.of(
                                0,
                                10
                        ),
                        2
                );


        when(
                productService.getAllProducts(
                        0,
                        10,
                        "id",
                        "asc"
                )
        ).thenReturn(productPage);


        mockMvc.perform(
                        get("/api/v1/products")
                )
                .andExpect(status().isOk());


        verify(productService)
                .getAllProducts(
                        0,
                        10,
                        "id",
                        "asc"
                );
    }


    /*
     * UPDATE PRODUCT
     */

    @Test
    void updateProduct_ShouldReturnUpdatedProduct()
            throws Exception {

        ProductRequest request =
                ProductRequest.builder()
                        .productName("Updated Laptop")
                        .items(
                                List.of(
                                        ItemRequest.builder()
                                                .quantity(20)
                                                .build()
                                )
                        )
                        .build();


        ProductResponse response =
                ProductResponse.builder()
                        .id(1L)
                        .productName("Updated Laptop")
                        .items(List.of())
                        .build();


        when(
                productService.updateProduct(
                        eq(1L),
                        any(ProductRequest.class)
                )
        ).thenReturn(response);


        mockMvc.perform(
                        put(
                                "/api/v1/products/{id}",
                                1L
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.productName")
                                .value("Updated Laptop")
                );


        verify(productService)
                .updateProduct(
                        eq(1L),
                        any(ProductRequest.class)
                );
    }


    /*
     * DELETE PRODUCT
     */

    @Test
    void deleteProduct_ShouldReturnNoContent()
            throws Exception {

        doNothing()
                .when(productService)
                .deleteProduct(1L);


        mockMvc.perform(
                        delete(
                                "/api/v1/products/{id}",
                                1L
                        )
                )
                .andExpect(status().isNoContent());


        verify(productService)
                .deleteProduct(1L);
    }


    /*
     * CREATE ITEM
     */

    @Test
    void createItem_ShouldReturnCreated()
            throws Exception {

        ItemRequest request =
                ItemRequest.builder()
                        .quantity(10)
                        .build();


        ItemResponse response =
                ItemResponse.builder()
                        .id(1L)
                        .quantity(10)
                        .build();


        when(
                itemService.createItem(
                        eq(1L),
                        any(ItemRequest.class)
                )
        ).thenReturn(response);


        mockMvc.perform(
                        post(
                                "/api/v1/products/{productId}/items",
                                1L
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.quantity").value(10));


        verify(itemService)
                .createItem(
                        eq(1L),
                        any(ItemRequest.class)
                );
    }


    /*
     * GET ITEMS BY PRODUCT ID
     */

    @Test
    void getItemsByProductId_ShouldReturnItems()
            throws Exception {

        List<ItemResponse> items =
                List.of(

                        ItemResponse.builder()
                                .id(1L)
                                .quantity(10)
                                .build(),

                        ItemResponse.builder()
                                .id(2L)
                                .quantity(20)
                                .build()
                );


        when(
                itemService.getItemsByProductId(1L)
        ).thenReturn(items);


        mockMvc.perform(
                        get(
                                "/api/v1/products/{productId}/items",
                                1L
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(
                        jsonPath("$[0].quantity")
                                .value(10)
                )
                .andExpect(
                        jsonPath("$[1].quantity")
                                .value(20)
                );


        verify(itemService)
                .getItemsByProductId(1L);
    }


    /*
     * SEARCH PRODUCTS
     */

    @Test
    void searchProducts_ShouldReturnMatchingProducts()
            throws Exception {

        ProductResponse product =
                ProductResponse.builder()
                        .id(1L)
                        .productName("Laptop")
                        .items(List.of())
                        .build();


        Page<ProductResponse> productPage =
                new PageImpl<>(
                        List.of(product),
                        PageRequest.of(
                                0,
                                10
                        ),
                        1
                );


        when(
                productService.searchProducts(
                        "lap",
                        0,
                        10,
                        "id",
                        "asc"
                )
        ).thenReturn(productPage);


        mockMvc.perform(
                        get(
                                "/api/v1/products/search"
                        )
                                .param(
                                        "keyword",
                                        "lap"
                                )
                )
                .andExpect(status().isOk());


        verify(productService)
                .searchProducts(
                        "lap",
                        0,
                        10,
                        "id",
                        "asc"
                );
    }


    /*
     * FILTER BY MINIMUM QUANTITY
     */

    @Test
    void filterProductsByMinimumQuantity_ShouldReturnProducts()
            throws Exception {

        ProductResponse product =
                ProductResponse.builder()
                        .id(1L)
                        .productName("Laptop")
                        .items(List.of())
                        .build();


        Page<ProductResponse> productPage =
                new PageImpl<>(
                        List.of(product),
                        PageRequest.of(
                                0,
                                10
                        ),
                        1
                );


        when(
                productService.filterProductsByMinimumQuantity(
                        5,
                        0,
                        10,
                        "id",
                        "asc"
                )
        ).thenReturn(productPage);


        mockMvc.perform(
                        get(
                                "/api/v1/products/filter"
                        )
                                .param(
                                        "minQuantity",
                                        "5"
                                )
                )
                .andExpect(status().isOk());


        verify(productService)
                .filterProductsByMinimumQuantity(
                        5,
                        0,
                        10,
                        "id",
                        "asc"
                );
    }


    /*
     * FILTER BY QUANTITY RANGE
     */

    @Test
    void filterProductsByQuantityRange_ShouldReturnProducts()
            throws Exception {

        ProductResponse product =
                ProductResponse.builder()
                        .id(1L)
                        .productName("Laptop")
                        .items(List.of())
                        .build();


        Page<ProductResponse> productPage =
                new PageImpl<>(
                        List.of(product),
                        PageRequest.of(
                                0,
                                10
                        ),
                        1
                );


        when(
                productService.filterProductsByQuantityRange(
                        5,
                        20,
                        0,
                        10,
                        "id",
                        "asc"
                )
        ).thenReturn(productPage);


        mockMvc.perform(
                        get(
                                "/api/v1/products/filter/range"
                        )
                                .param(
                                        "minQuantity",
                                        "5"
                                )
                                .param(
                                        "maxQuantity",
                                        "20"
                                )
                )
                .andExpect(status().isOk());


        verify(productService)
                .filterProductsByQuantityRange(
                        5,
                        20,
                        0,
                        10,
                        "id",
                        "asc"
                );
    }


    /*
     * INVALID QUANTITY
     */

    @Test
    void createProduct_WithInvalidItemQuantity_ShouldReturnBadRequest()
            throws Exception {

        ProductRequest request =
                ProductRequest.builder()
                        .productName("Laptop")
                        .items(
                                List.of(
                                        ItemRequest.builder()
                                                .quantity(0)
                                                .build()
                                )
                        )
                        .build();


        mockMvc.perform(
                        post("/api/v1/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath(
                                "$.validationErrors['items[0].quantity']"
                        )
                        .value("Quantity must be greater than 0")
                );


        verifyNoInteractions(productService);
    }
}