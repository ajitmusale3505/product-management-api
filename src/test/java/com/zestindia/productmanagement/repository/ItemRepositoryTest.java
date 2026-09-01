package com.zestindia.productmanagement.repository;

import com.zestindia.productmanagement.entity.Item;
import com.zestindia.productmanagement.entity.Product;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
class ItemRepositoryTest {


    @Autowired
    private ItemRepository itemRepository;


    @Autowired
    private ProductRepository productRepository;


    /*
     * ==========================================
     * SAVE ITEM TEST
     * ==========================================
     */

    @Test
    void shouldSaveItemSuccessfully() {

        // GIVEN

        Product product = Product.builder()

                .productName("Laptop")

                .createdBy("ajit")

                .createdOn(LocalDateTime.now())

                .build();


        Product savedProduct =

                productRepository.save(product);


        Item item = Item.builder()

                .quantity(10)

                .product(savedProduct)

                .build();


        // WHEN

        Item savedItem =

                itemRepository.save(item);


        // THEN

        assertTrue(

                savedItem.getId() != null
        );


        assertEquals(

                10,

                savedItem.getQuantity()
        );


        assertEquals(

                savedProduct.getId(),

                savedItem.getProduct().getId()
        );
    }


    /*
     * ==========================================
     * FIND ITEMS BY PRODUCT ID
     * ==========================================
     */

    @Test
    void shouldFindItemsByProductId() {

        // GIVEN

        Product product = Product.builder()

                .productName("Mobile")

                .createdBy("ajit")

                .createdOn(LocalDateTime.now())

                .build();


        Product savedProduct =

                productRepository.save(product);


        Item item1 = Item.builder()

                .quantity(5)

                .product(savedProduct)

                .build();


        Item item2 = Item.builder()

                .quantity(10)

                .product(savedProduct)

                .build();


        itemRepository.save(item1);

        itemRepository.save(item2);


        // WHEN

        List<Item> items =

                itemRepository.findByProductId(

                        savedProduct.getId()
                );


        // THEN

        assertEquals(

                2,

                items.size()
        );


        assertTrue(

                items.stream()

                        .anyMatch(

                                item ->

                                        item.getQuantity()

                                                .equals(5)
                        )
        );


        assertTrue(

                items.stream()

                        .anyMatch(

                                item ->

                                        item.getQuantity()

                                                .equals(10)
                        )
        );
    }


    /*
     * ==========================================
     * FIND ITEMS BY PRODUCT ID
     * WHEN NO ITEMS EXIST
     * ==========================================
     */

    @Test
    void shouldReturnEmptyListWhenProductHasNoItems() {

        // GIVEN

        Product product = Product.builder()

                .productName("Keyboard")

                .createdBy("ajit")

                .createdOn(LocalDateTime.now())

                .build();


        Product savedProduct =

                productRepository.save(product);


        // WHEN

        List<Item> items =

                itemRepository.findByProductId(

                        savedProduct.getId()
                );


        // THEN

        assertTrue(

                items.isEmpty()
        );
    }


    /*
     * ==========================================
     * FIND ITEM BY ID
     * ==========================================
     */

    @Test
    void shouldFindItemById() {

        // GIVEN

        Product product = Product.builder()

                .productName("Monitor")

                .createdBy("ajit")

                .createdOn(LocalDateTime.now())

                .build();


        Product savedProduct =

                productRepository.save(product);


        Item item = Item.builder()

                .quantity(15)

                .product(savedProduct)

                .build();


        Item savedItem =

                itemRepository.save(item);


        // WHEN

        Optional<Item> foundItem =

                itemRepository.findById(

                        savedItem.getId()
                );


        // THEN

        assertTrue(

                foundItem.isPresent()
        );


        assertEquals(

                15,

                foundItem.get()

                        .getQuantity()
        );
    }


    /*
     * ==========================================
     * FIND NON-EXISTING ITEM
     * ==========================================
     */

    @Test
    void shouldReturnEmptyOptionalForNonExistingItem() {

        // WHEN

        Optional<Item> item =

                itemRepository.findById(

                        9999L
                );


        // THEN

        assertFalse(

                item.isPresent()
        );
    }
}