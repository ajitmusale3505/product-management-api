package com.zestindia.productmanagement.service.impl;

import com.zestindia.productmanagement.dto.request.ItemRequest;
import com.zestindia.productmanagement.dto.response.ItemResponse;
import com.zestindia.productmanagement.entity.Item;
import com.zestindia.productmanagement.entity.Product;
import com.zestindia.productmanagement.exception.ResourceNotFoundException;
import com.zestindia.productmanagement.repository.ItemRepository;
import com.zestindia.productmanagement.repository.ProductRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {


    @Mock
    private ItemRepository itemRepository;


    @Mock
    private ProductRepository productRepository;


    @Mock
    private ItemRequest itemRequest;


    @Mock
    private Product product;


    @InjectMocks
    private ItemServiceImpl itemService;


    /*
     * ==========================================
     * CREATE ITEM TESTS
     * ==========================================
     */


    @Test
    void shouldCreateItemSuccessfully() {

        // GIVEN

        Long productId = 1L;

        when(
                productRepository.findById(productId)
        ).thenReturn(
                Optional.of(product)
        );

        when(
                itemRequest.getQuantity()
        ).thenReturn(10);


        Item savedItem = Item.builder()

                .id(1L)

                .quantity(10)

                .build();


        when(
                itemRepository.save(
                        any(Item.class)
                )
        ).thenReturn(
                savedItem
        );


        // WHEN

        ItemResponse response =

                itemService.createItem(

                        productId,

                        itemRequest
                );


        // THEN

        assertEquals(

                1L,

                response.getId()
        );


        assertEquals(

                10,

                response.getQuantity()
        );


        verify(

                productRepository

        ).findById(productId);


        verify(

                product

        ).addItem(

                any(Item.class)
        );


        verify(

                itemRepository

        ).save(

                any(Item.class)
        );
    }


    @Test
    void shouldThrowExceptionWhenCreatingItemForNonExistingProduct() {

        // GIVEN

        Long productId = 1L;


        when(

                productRepository.findById(productId)

        ).thenReturn(

                Optional.empty()
        );


        // WHEN

        ResourceNotFoundException exception =

                assertThrows(

                        ResourceNotFoundException.class,

                        () ->

                                itemService.createItem(

                                        productId,

                                        itemRequest
                                )
                );


        // THEN

        assertEquals(

                "Product not found with id: " + productId,

                exception.getMessage()
        );


        verify(

                itemRepository,

                never()

        ).save(

                any(Item.class)
        );
    }


    /*
     * ==========================================
     * GET ITEMS BY PRODUCT ID TESTS
     * ==========================================
     */


    @Test
    void shouldGetItemsByProductIdSuccessfully() {

        // GIVEN

        Long productId = 1L;


        when(

                productRepository.existsById(productId)

        ).thenReturn(

                true
        );


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


        when(

                itemRepository.findByProductId(productId)

        ).thenReturn(

                List.of(

                        item1,

                        item2
                )
        );


        // WHEN

        List<ItemResponse> responses =

                itemService.getItemsByProductId(

                        productId
                );


        // THEN

        assertEquals(

                2,

                responses.size()
        );


        assertEquals(

                1L,

                responses.get(0).getId()
        );


        assertEquals(

                10,

                responses.get(0).getQuantity()
        );


        assertEquals(

                2L,

                responses.get(1).getId()
        );


        assertEquals(

                20,

                responses.get(1).getQuantity()
        );


        verify(

                productRepository

        ).existsById(productId);


        verify(

                itemRepository

        ).findByProductId(productId);
    }


    @Test
    void shouldThrowExceptionWhenGettingItemsForNonExistingProduct() {

        // GIVEN

        Long productId = 1L;


        when(

                productRepository.existsById(productId)

        ).thenReturn(

                false
        );


        // WHEN

        ResourceNotFoundException exception =

                assertThrows(

                        ResourceNotFoundException.class,

                        () ->

                                itemService.getItemsByProductId(

                                        productId
                                )
                );


        // THEN

        assertEquals(

                "Product not found with id: " + productId,

                exception.getMessage()
        );


        verify(

                productRepository

        ).existsById(productId);


        verify(

                itemRepository,

                never()

        ).findByProductId(

                any()
        );
    }
}