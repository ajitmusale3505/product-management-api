package com.zestindia.productmanagement.repository;

import com.zestindia.productmanagement.entity.Item;
import com.zestindia.productmanagement.entity.Product;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;


    /*
     * =====================================
     * SEARCH PRODUCT TESTS
     * =====================================
     */


    @Test
    void shouldFindProductsByProductNameIgnoringCase() {

        Product laptop =
                createProduct(
                        "Laptop",
                        10
                );

        Product mobile =
                createProduct(
                        "Mobile",
                        20
                );


        productRepository.saveAll(
                List.of(
                        laptop,
                        mobile
                )
        );


        Page<Product> result =
                productRepository
                        .findByProductNameContainingIgnoreCase(

                                "LAP",

                                PageRequest.of(
                                        0,
                                        10
                                )
                        );


        assertNotNull(result);

        assertEquals(
                1,
                result.getContent().size()
        );

        assertEquals(
                "Laptop",
                result.getContent()
                        .get(0)
                        .getProductName()
        );
    }


    @Test
    void shouldReturnEmptyPageWhenProductNameDoesNotMatch() {

        Product laptop =
                createProduct(
                        "Laptop",
                        10
                );


        productRepository.save(
                laptop
        );


        Page<Product> result =
                productRepository
                        .findByProductNameContainingIgnoreCase(

                                "Television",

                                PageRequest.of(
                                        0,
                                        10
                                )
                        );


        assertTrue(
                result.isEmpty()
        );
    }


    /*
     * =====================================
     * FILTER BY MINIMUM QUANTITY TESTS
     * =====================================
     */


    @Test
    void shouldFindProductsByMinimumQuantity() {

        Product laptop =
                createProduct(
                        "Laptop",
                        10
                );

        Product mobile =
                createProduct(
                        "Mobile",
                        50
                );

        Product keyboard =
                createProduct(
                        "Keyboard",
                        5
                );


        productRepository.saveAll(
                List.of(
                        laptop,
                        mobile,
                        keyboard
                )
        );


        Page<Product> result =
                productRepository
                        .findProductsByMinimumQuantity(

                                10,

                                PageRequest.of(
                                        0,
                                        10
                                )
                        );


        assertEquals(
                2,
                result.getContent().size()
        );


        assertTrue(

                result.getContent()

                        .stream()

                        .anyMatch(

                                product ->

                                        product

                                                .getProductName()

                                                .equals("Laptop")

                        )

        );


        assertTrue(

                result.getContent()

                        .stream()

                        .anyMatch(

                                product ->

                                        product

                                                .getProductName()

                                                .equals("Mobile")

                        )

        );
    }


    @Test
    void shouldReturnEmptyPageWhenNoProductMatchesMinimumQuantity() {

        Product laptop =
                createProduct(
                        "Laptop",
                        10
                );


        productRepository.save(
                laptop
        );


        Page<Product> result =
                productRepository
                        .findProductsByMinimumQuantity(

                                100,

                                PageRequest.of(
                                        0,
                                        10
                                )
                        );


        assertTrue(
                result.isEmpty()
        );
    }


    /*
     * =====================================
     * FILTER BY QUANTITY RANGE TESTS
     * =====================================
     */


    @Test
    void shouldFindProductsByQuantityRange() {

        Product laptop =
                createProduct(
                        "Laptop",
                        10
                );

        Product mobile =
                createProduct(
                        "Mobile",
                        30
                );

        Product keyboard =
                createProduct(
                        "Keyboard",
                        50
                );


        productRepository.saveAll(
                List.of(
                        laptop,
                        mobile,
                        keyboard
                )
        );


        Page<Product> result =
                productRepository
                        .findProductsByQuantityRange(

                                10,

                                30,

                                PageRequest.of(
                                        0,
                                        10
                                )
                        );


        assertEquals(
                2,
                result.getContent().size()
        );


        assertTrue(

                result.getContent()

                        .stream()

                        .anyMatch(

                                product ->

                                        product

                                                .getProductName()

                                                .equals("Laptop")

                        )

        );


        assertTrue(

                result.getContent()

                        .stream()

                        .anyMatch(

                                product ->

                                        product

                                                .getProductName()

                                                .equals("Mobile")

                        )

        );
    }


    @Test
    void shouldIncludeBoundaryValuesInQuantityRange() {

        Product minimumProduct =
                createProduct(
                        "Minimum Product",
                        10
                );

        Product maximumProduct =
                createProduct(
                        "Maximum Product",
                        50
                );


        productRepository.saveAll(

                List.of(

                        minimumProduct,

                        maximumProduct

                )

        );


        Page<Product> result =

                productRepository

                        .findProductsByQuantityRange(

                                10,

                                50,

                                PageRequest.of(

                                        0,

                                        10

                                )

                        );


        assertEquals(

                2,

                result.getContent().size()

        );
    }


    @Test
    void shouldReturnEmptyPageWhenNoProductMatchesQuantityRange() {

        Product laptop =
                createProduct(
                        "Laptop",
                        10
                );


        productRepository.save(
                laptop
        );


        Page<Product> result =
                productRepository
                        .findProductsByQuantityRange(

                                50,

                                100,

                                PageRequest.of(
                                        0,
                                        10
                                )
                        );


        assertTrue(
                result.isEmpty()
        );
    }


    /*
     * =====================================
     * PAGINATION TEST
     * =====================================
     */


    @Test
    void shouldApplyPaginationCorrectly() {

        Product product1 =
                createProduct(
                        "Product 1",
                        10
                );

        Product product2 =
                createProduct(
                        "Product 2",
                        20
                );

        Product product3 =
                createProduct(
                        "Product 3",
                        30
                );


        productRepository.saveAll(

                List.of(

                        product1,

                        product2,

                        product3

                )

        );


        Page<Product> result =

                productRepository

                        .findProductsByMinimumQuantity(

                                1,

                                PageRequest.of(

                                        0,

                                        2

                                )

                        );


        assertEquals(

                2,

                result.getContent().size()

        );


        assertEquals(

                3,

                result.getTotalElements()

        );


        assertEquals(

                2,

                result.getTotalPages()

        );
    }


    /*
     * =====================================
     * HELPER METHOD
     * =====================================
     */


    private Product createProduct(

            String productName,

            Integer quantity

    ) {

        Product product =

                Product.builder()

                        .productName(
                                productName
                        )

                        .createdBy(
                                "admin"
                        )

                        .createdOn(
                                LocalDateTime.now()
                        )

                        .build();


        Item item =

                Item.builder()

                        .quantity(
                                quantity
                        )

                        .build();


        product.addItem(
                item
        );


        return product;
    }
}