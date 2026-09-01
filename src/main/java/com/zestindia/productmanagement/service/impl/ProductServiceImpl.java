package com.zestindia.productmanagement.service.impl;

import com.zestindia.productmanagement.dto.request.ItemRequest;
import com.zestindia.productmanagement.dto.request.ProductRequest;

import com.zestindia.productmanagement.dto.response.ItemResponse;
import com.zestindia.productmanagement.dto.response.ProductResponse;

import com.zestindia.productmanagement.entity.Item;
import com.zestindia.productmanagement.entity.Product;

import com.zestindia.productmanagement.exception.ResourceNotFoundException;

import com.zestindia.productmanagement.repository.ProductRepository;

import com.zestindia.productmanagement.service.ProductService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {


    private final ProductRepository productRepository;


    /*
     * ================================
     * CREATE PRODUCT
     * ================================
     */

    @Override
    public ProductResponse createProduct(
            ProductRequest request
    ) {

        Product product = Product.builder()

                .productName(
                        request.getProductName()
                )

                .createdBy(
                        getCurrentUsername()
                )

                .createdOn(
                        LocalDateTime.now()
                )

                .build();


        addItemsToProduct(
                product,
                request.getItems()
        );


        Product savedProduct =
                productRepository.save(product);


        return mapToProductResponse(
                savedProduct
        );
    }


    /*
     * ================================
     * GET PRODUCT BY ID
     * ================================
     */

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(
            Long id
    ) {

        Product product =
                findProductById(id);


        return mapToProductResponse(
                product
        );
    }


    /*
     * ================================
     * GET ALL PRODUCTS
     * ================================
     */

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(

            int page,

            int size,

            String sortBy,

            String sortDirection

    ) {

        Pageable pageable =
                createPageable(

                        page,

                        size,

                        sortBy,

                        sortDirection

                );


        return productRepository

                .findAll(pageable)

                .map(
                        this::mapToProductResponse
                );
    }


    /*
     * ================================
     * UPDATE PRODUCT
     * ================================
     */

    @Override
    public ProductResponse updateProduct(

            Long id,

            ProductRequest request

    ) {

        Product product =
                findProductById(id);


        /*
         * Update Product Name
         */

        product.setProductName(

                request.getProductName()

        );


        /*
         * Remove old items
         */

        product.clearItems();


        /*
         * Add new items
         */

        addItemsToProduct(

                product,

                request.getItems()

        );


        /*
         * Audit Fields
         */

        product.setModifiedBy(

                getCurrentUsername()

        );


        product.setModifiedOn(

                LocalDateTime.now()

        );


        Product updatedProduct =
                productRepository.save(product);


        return mapToProductResponse(

                updatedProduct

        );
    }


    /*
     * ================================
     * DELETE PRODUCT
     * ================================
     */

    @Override
    public void deleteProduct(
            Long id
    ) {

        Product product =
                findProductById(id);


        productRepository.delete(
                product
        );
    }


    /*
     * ================================
     * SEARCH PRODUCTS
     * ================================
     */

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(

            String keyword,

            int page,

            int size,

            String sortBy,

            String sortDirection

    ) {

        Pageable pageable =
                createPageable(

                        page,

                        size,

                        sortBy,

                        sortDirection

                );


        Page<Product> productPage =

                productRepository

                        .findByProductNameContainingIgnoreCase(

                                keyword,

                                pageable

                        );


        return productPage.map(

                this::mapToProductResponse

        );
    }


    /*
     * ================================
     * FILTER BY MINIMUM QUANTITY
     * ================================
     */

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse>
    filterProductsByMinimumQuantity(

            Integer minQuantity,

            int page,

            int size,

            String sortBy,

            String sortDirection

    ) {

        if (minQuantity == null) {

            throw new IllegalArgumentException(

                    "Minimum quantity is required"

            );
        }


        if (minQuantity < 0) {

            throw new IllegalArgumentException(

                    "Minimum quantity cannot be negative"

            );
        }


        Pageable pageable =

                createPageable(

                        page,

                        size,

                        sortBy,

                        sortDirection

                );


        Page<Product> productPage =

                productRepository

                        .findProductsByMinimumQuantity(

                                minQuantity,

                                pageable

                        );


        return productPage.map(

                this::mapToProductResponse

        );
    }


    /*
     * ================================
     * FILTER BY QUANTITY RANGE
     * ================================
     */

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse>
    filterProductsByQuantityRange(

            Integer minQuantity,

            Integer maxQuantity,

            int page,

            int size,

            String sortBy,

            String sortDirection

    ) {

        if (

                minQuantity == null

                        ||

                maxQuantity == null

        ) {

            throw new IllegalArgumentException(

                    "Minimum quantity and maximum quantity are required"

            );
        }


        if (

                minQuantity < 0

                        ||

                maxQuantity < 0

        ) {

            throw new IllegalArgumentException(

                    "Quantity cannot be negative"

            );
        }


        if (

                minQuantity > maxQuantity

        ) {

            throw new IllegalArgumentException(

                    "Minimum quantity cannot be greater than maximum quantity"

            );
        }


        Pageable pageable =

                createPageable(

                        page,

                        size,

                        sortBy,

                        sortDirection

                );


        Page<Product> productPage =

                productRepository

                        .findProductsByQuantityRange(

                                minQuantity,

                                maxQuantity,

                                pageable

                        );


        return productPage.map(

                this::mapToProductResponse

        );
    }


    /*
     * ================================
     * FIND PRODUCT
     * ================================
     */

    private Product findProductById(
            Long id
    ) {

        return productRepository

                .findById(id)

                .orElseThrow(

                        () ->

                                new ResourceNotFoundException(

                                        "Product not found with id: "

                                                + id

                                )

                );
    }


    /*
     * ================================
     * ADD ITEMS
     * ================================
     */

    private void addItemsToProduct(

            Product product,

            List<ItemRequest> itemRequests

    ) {

        if (

                itemRequests == null

                        ||

                itemRequests.isEmpty()

        ) {

            return;
        }


        for (

                ItemRequest itemRequest

                        :

                itemRequests

        ) {

            Item item =

                    Item.builder()

                            .quantity(

                                    itemRequest.getQuantity()

                            )

                            .build();


            product.addItem(
                    item
            );
        }
    }


    /*
     * ================================
     * CREATE PAGEABLE
     * ================================
     */

    private Pageable createPageable(

            int page,

            int size,

            String sortBy,

            String sortDirection

    ) {

        validatePagination(

                page,

                size

        );


        validateSortField(

                sortBy

        );


        Sort.Direction direction =

                "desc".equalsIgnoreCase(

                        sortDirection

                )

                        ?

                        Sort.Direction.DESC

                        :

                        Sort.Direction.ASC;


        Sort sort =

                Sort.by(

                        direction,

                        sortBy

                );


        return PageRequest.of(

                page,

                size,

                sort

        );
    }


    /*
     * ================================
     * VALIDATE PAGINATION
     * ================================
     */

    private void validatePagination(

            int page,

            int size

    ) {

        if (page < 0) {

            throw new IllegalArgumentException(

                    "Page number cannot be negative"

            );
        }


        if (size <= 0) {

            throw new IllegalArgumentException(

                    "Page size must be greater than 0"

            );
        }
    }


    /*
     * ================================
     * VALIDATE SORT FIELD
     * ================================
     */

    private void validateSortField(

            String sortBy

    ) {

        Set<String> allowedFields =

                Set.of(

                        "id",

                        "productName",

                        "createdBy",

                        "createdOn",

                        "modifiedBy",

                        "modifiedOn"

                );


        if (

                sortBy == null

                        ||

                !allowedFields.contains(

                        sortBy

                )

        ) {

            throw new IllegalArgumentException(

                    "Invalid sort field: "

                            + sortBy

            );
        }
    }


    /*
     * ================================
     * GET CURRENT USER
     * ================================
     */

    private String getCurrentUsername() {

        Authentication authentication =

                SecurityContextHolder

                        .getContext()

                        .getAuthentication();


        if (

                authentication == null

                        ||

                !authentication.isAuthenticated()

                        ||

                authentication

                        instanceof

                        AnonymousAuthenticationToken

        ) {

            return "SYSTEM";
        }


        return authentication.getName();
    }


    /*
     * ================================
     * MAP TO RESPONSE
     * ================================
     */

    private ProductResponse mapToProductResponse(

            Product product

    ) {

        List<ItemResponse> itemResponses =

                product.getItems()

                        .stream()

                        .map(

                                item ->

                                        ItemResponse.builder()

                                                .id(

                                                        item.getId()

                                                )

                                                .quantity(

                                                        item.getQuantity()

                                                )

                                                .build()

                        )

                        .toList();


        return ProductResponse.builder()

                .id(

                        product.getId()

                )

                .productName(

                        product.getProductName()

                )

                .createdBy(

                        product.getCreatedBy()

                )

                .createdOn(

                        product.getCreatedOn()

                )

                .modifiedBy(

                        product.getModifiedBy()

                )

                .modifiedOn(

                        product.getModifiedOn()

                )

                .items(

                        itemResponses

                )

                .build();
    }

}