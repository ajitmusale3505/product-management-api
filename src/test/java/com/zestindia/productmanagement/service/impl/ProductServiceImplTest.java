package com.zestindia.productmanagement.service.impl;

import com.zestindia.productmanagement.dto.request.ItemRequest;

import com.zestindia.productmanagement.dto.request.ProductRequest;
import com.zestindia.productmanagement.dto.response.ProductResponse;

import com.zestindia.productmanagement.entity.Item;
import com.zestindia.productmanagement.entity.Product;

import com.zestindia.productmanagement.exception.ResourceNotFoundException;

import com.zestindia.productmanagement.repository.ProductRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {


    @Mock
    private ProductRepository productRepository;


    @InjectMocks
    private ProductServiceImpl productService;


    @BeforeEach
    void setUp() {

        UsernamePasswordAuthenticationToken authentication =

                new UsernamePasswordAuthenticationToken(

                        "admin",

                        null,

                        List.of()

                );


        SecurityContextHolder

                .getContext()

                .setAuthentication(

                        authentication

                );
    }


    @AfterEach
    void tearDown() {

        SecurityContextHolder

                .clearContext();
    }


    /*
     * =====================================
     * CREATE PRODUCT TESTS
     * =====================================
     */


    @Test
    void shouldCreateProductSuccessfully() {


        ItemRequest itemRequest1 =

                ItemRequest.builder()

                        .quantity(10)

                        .build();


        ItemRequest itemRequest2 =

                ItemRequest.builder()

                        .quantity(20)

                        .build();


        ProductRequest request =

                ProductRequest.builder()

                        .productName("Laptop")

                        .items(

                                List.of(

                                        itemRequest1,

                                        itemRequest2

                                )

                        )

                        .build();


        Product savedProduct =

                Product.builder()

                        .id(1L)

                        .productName("Laptop")

                        .createdBy("admin")

                        .createdOn(

                                LocalDateTime.now()

                        )

                        .build();


        Item item1 =

                Item.builder()

                        .id(1L)

                        .quantity(10)

                        .build();


        Item item2 =

                Item.builder()

                        .id(2L)

                        .quantity(20)

                        .build();


        savedProduct.addItem(item1);

        savedProduct.addItem(item2);


        when(

                productRepository.save(

                        any(Product.class)

                )

        )

                .thenReturn(

                        savedProduct

                );


        ProductResponse response =

                productService.createProduct(

                        request

                );


        assertNotNull(response);

        assertEquals(

                1L,

                response.getId()

        );

        assertEquals(

                "Laptop",

                response.getProductName()

        );

        assertEquals(

                "admin",

                response.getCreatedBy()

        );

        assertEquals(

                2,

                response.getItems().size()

        );


        verify(

                productRepository,

                times(1)

        )

                .save(

                        any(Product.class)

                );
    }


    @Test
    void shouldCreateProductWithoutItems() {


        ProductRequest request =

                ProductRequest.builder()

                        .productName("Mobile")

                        .items(null)

                        .build();


        Product savedProduct =

                Product.builder()

                        .id(1L)

                        .productName("Mobile")

                        .createdBy("admin")

                        .createdOn(

                                LocalDateTime.now()

                        )

                        .build();


        when(

                productRepository.save(

                        any(Product.class)

                )

        )

                .thenReturn(

                        savedProduct

                );


        ProductResponse response =

                productService.createProduct(

                        request

                );


        assertNotNull(response);

        assertEquals(

                "Mobile",

                response.getProductName()

        );

        assertTrue(

                response.getItems().isEmpty()

        );


        verify(

                productRepository

        )

                .save(

                        any(Product.class)

                );
    }


    /*
     * =====================================
     * GET PRODUCT BY ID TESTS
     * =====================================
     */


    @Test
    void shouldGetProductByIdSuccessfully() {


        Product product =

                createSampleProduct();


        when(

                productRepository.findById(1L)

        )

                .thenReturn(

                        Optional.of(product)

                );


        ProductResponse response =

                productService.getProductById(

                        1L

                );


        assertNotNull(response);

        assertEquals(

                1L,

                response.getId()

        );

        assertEquals(

                "Laptop",

                response.getProductName()

        );


        verify(

                productRepository

        )

                .findById(

                        1L

                );
    }


    @Test
    void shouldThrowExceptionWhenProductNotFound() {


        when(

                productRepository.findById(1L)

        )

                .thenReturn(

                        Optional.empty()

                );


        ResourceNotFoundException exception =

                assertThrows(

                        ResourceNotFoundException.class,

                        () ->

                                productService

                                        .getProductById(

                                                1L

                                        )

                );


        assertEquals(

                "Product not found with id: 1",

                exception.getMessage()

        );


        verify(

                productRepository

        )

                .findById(

                        1L

                );
    }


    /*
     * =====================================
     * GET ALL PRODUCTS TEST
     * =====================================
     */


    @Test
    void shouldGetAllProductsSuccessfully() {


        Product product1 =

                createSampleProduct();


        Product product2 =

                Product.builder()

                        .id(2L)

                        .productName("Mobile")

                        .createdBy("admin")

                        .createdOn(

                                LocalDateTime.now()

                        )

                        .build();


        Page<Product> productPage =

                new PageImpl<>(

                        List.of(

                                product1,

                                product2

                        )

                );


        when(

                productRepository.findAll(

                        any(Pageable.class)

                )

        )

                .thenReturn(

                        productPage

                );


        Page<ProductResponse> response =

                productService.getAllProducts(

                        0,

                        10,

                        "productName",

                        "asc"

                );


        assertNotNull(response);

        assertEquals(

                2,

                response.getContent().size()

        );

        assertEquals(

                "Laptop",

                response.getContent()

                        .get(0)

                        .getProductName()

        );


        verify(

                productRepository

        )

                .findAll(

                        any(Pageable.class)

                );
    }


    /*
     * =====================================
     * UPDATE PRODUCT TESTS
     * =====================================
     */


    @Test
    void shouldUpdateProductSuccessfully() {


        Product existingProduct =

                createSampleProduct();


        when(

                productRepository.findById(

                        1L

                )

        )

                .thenReturn(

                        Optional.of(

                                existingProduct

                        )

                );


        ItemRequest itemRequest =

                ItemRequest.builder()

                        .quantity(50)

                        .build();


        ProductRequest request =

                ProductRequest.builder()

                        .productName(

                                "Updated Laptop"

                        )

                        .items(

                                List.of(

                                        itemRequest

                                )

                        )

                        .build();


        when(

                productRepository.save(

                        any(Product.class)

                )

        )

                .thenAnswer(

                        invocation ->

                                invocation.getArgument(

                                        0

                                )

                );


        ProductResponse response =

                productService.updateProduct(

                        1L,

                        request

                );


        assertNotNull(response);

        assertEquals(

                "Updated Laptop",

                response.getProductName()

        );

        assertEquals(

                "admin",

                response.getModifiedBy()

        );

        assertNotNull(

                response.getModifiedOn()

        );

        assertEquals(

                1,

                response.getItems().size()

        );

        assertEquals(

                50,

                response.getItems()

                        .get(0)

                        .getQuantity()

        );


        verify(

                productRepository

        )

                .save(

                        existingProduct

                );
    }


    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingProduct() {


        when(

                productRepository.findById(

                        100L

                )

        )

                .thenReturn(

                        Optional.empty()

                );


        ProductRequest request =

                ProductRequest.builder()

                        .productName(

                                "Updated Product"

                        )

                        .build();


        assertThrows(

                ResourceNotFoundException.class,

                () ->

                        productService.updateProduct(

                                100L,

                                request

                        )

        );


        verify(

                productRepository,

                never()

        )

                .save(

                        any(Product.class)

                );
    }


    /*
     * =====================================
     * DELETE PRODUCT TESTS
     * =====================================
     */


    @Test
    void shouldDeleteProductSuccessfully() {


        Product product =

                createSampleProduct();


        when(

                productRepository.findById(

                        1L

                )

        )

                .thenReturn(

                        Optional.of(

                                product

                        )

                );


        productService.deleteProduct(

                1L

        );


        verify(

                productRepository

        )

                .delete(

                        product

                );
    }


    @Test
    void shouldThrowExceptionWhenDeletingNonExistingProduct() {


        when(

                productRepository.findById(

                        100L

                )

        )

                .thenReturn(

                        Optional.empty()

                );


        assertThrows(

                ResourceNotFoundException.class,

                () ->

                        productService.deleteProduct(

                                100L

                        )

        );


        verify(

                productRepository,

                never()

        )

                .delete(

                        any(Product.class)

                );
    }


    /*
     * =====================================
     * SEARCH PRODUCTS TEST
     * =====================================
     */


    @Test
    void shouldSearchProductsSuccessfully() {


        Product product =

                createSampleProduct();


        Page<Product> productPage =

                new PageImpl<>(

                        List.of(

                                product

                        )

                );


        when(

                productRepository

                        .findByProductNameContainingIgnoreCase(

                                eq("lap"),

                                any(Pageable.class)

                        )

        )

                .thenReturn(

                        productPage

                );


        Page<ProductResponse> response =

                productService.searchProducts(

                        "lap",

                        0,

                        10,

                        "productName",

                        "asc"

                );


        assertEquals(

                1,

                response.getContent().size()

        );

        assertEquals(

                "Laptop",

                response.getContent()

                        .get(0)

                        .getProductName()

        );


        verify(

                productRepository

        )

                .findByProductNameContainingIgnoreCase(

                        eq("lap"),

                        any(Pageable.class)

                );
    }


    /*
     * =====================================
     * FILTER BY MINIMUM QUANTITY
     * =====================================
     */


    @Test
    void shouldFilterProductsByMinimumQuantity() {


        Product product =

                createSampleProduct();


        Page<Product> productPage =

                new PageImpl<>(

                        List.of(

                                product

                        )

                );


        when(

                productRepository

                        .findProductsByMinimumQuantity(

                                eq(10),

                                any(Pageable.class)

                        )

        )

                .thenReturn(

                        productPage

                );


        Page<ProductResponse> response =

                productService

                        .filterProductsByMinimumQuantity(

                                10,

                                0,

                                10,

                                "productName",

                                "asc"

                        );


        assertEquals(

                1,

                response.getContent().size()

        );


        verify(

                productRepository

        )

                .findProductsByMinimumQuantity(

                        eq(10),

                        any(Pageable.class)

                );
    }


    /*
     * =====================================
     * FILTER BY QUANTITY RANGE
     * =====================================
     */


    @Test
    void shouldFilterProductsByQuantityRange() {


        Product product =

                createSampleProduct();


        Page<Product> productPage =

                new PageImpl<>(

                        List.of(

                                product

                        )

                );


        when(

                productRepository

                        .findProductsByQuantityRange(

                                eq(10),

                                eq(50),

                                any(Pageable.class)

                        )

        )

                .thenReturn(

                        productPage

                );


        Page<ProductResponse> response =

                productService

                        .filterProductsByQuantityRange(

                                10,

                                50,

                                0,

                                10,

                                "productName",

                                "asc"

                        );


        assertEquals(

                1,

                response.getContent().size()

        );


        verify(

                productRepository

        )

                .findProductsByQuantityRange(

                        eq(10),

                        eq(50),

                        any(Pageable.class)

                );
    }


    @Test
    void shouldThrowExceptionWhenMinimumQuantityIsGreaterThanMaximumQuantity() {


        IllegalArgumentException exception =

                assertThrows(

                        IllegalArgumentException.class,

                        () ->

                                productService

                                        .filterProductsByQuantityRange(

                                                100,

                                                10,

                                                0,

                                                10,

                                                "productName",

                                                "asc"

                                        )

                );


        assertEquals(

                "Minimum quantity cannot be greater than maximum quantity",

                exception.getMessage()

        );


        verifyNoInteractions(

                productRepository

        );
    }


    @Test
    void shouldThrowExceptionWhenQuantityRangeIsNull() {


        IllegalArgumentException exception =

                assertThrows(

                        IllegalArgumentException.class,

                        () ->

                                productService

                                        .filterProductsByQuantityRange(

                                                null,

                                                10,

                                                0,

                                                10,

                                                "productName",

                                                "asc"

                                        )

                );


        assertEquals(

                "Minimum quantity and maximum quantity are required",

                exception.getMessage()

        );


        verifyNoInteractions(

                productRepository

        );
    }


    /*
     * =====================================
     * HELPER METHOD
     * =====================================
     */


    private Product createSampleProduct() {


        Product product =

                Product.builder()

                        .id(1L)

                        .productName("Laptop")

                        .createdBy("admin")

                        .createdOn(

                                LocalDateTime.now()

                        )

                        .build();


        Item item =

                Item.builder()

                        .id(1L)

                        .quantity(10)

                        .build();


        product.addItem(

                item

        );


        return product;
    }
}