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

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;


    @Override
    public ProductResponse createProduct(ProductRequest request) {

        String currentUsername = getCurrentUsername();

        Product product = Product.builder()
                .productName(request.getProductName())
                .createdBy(currentUsername)
                .createdOn(LocalDateTime.now())
                .build();


        /*
         * Convert ItemRequest objects
         * into Item entities
         */
        if (request.getItems() != null
                && !request.getItems().isEmpty()) {

            for (ItemRequest itemRequest : request.getItems()) {

                Item item = Item.builder()
                        .quantity(itemRequest.getQuantity())
                        .build();

                product.addItem(item);
            }
        }


        Product savedProduct =
                productRepository.save(product);

        return mapToProductResponse(savedProduct);
    }


    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {

        Product product =
                findProductById(id);

        return mapToProductResponse(product);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(

            int page,
            int size,
            String sortBy,
            String sortDirection

    ) {

        Sort sort =
                sortDirection.equalsIgnoreCase("desc")

                        ? Sort.by(sortBy).descending()

                        : Sort.by(sortBy).ascending();


        Pageable pageable =
                PageRequest.of(

                        page,
                        size,
                        sort

                );


        return productRepository

                .findAll(pageable)

                .map(
                        this::mapToProductResponse
                );
    }


    @Override
    public ProductResponse updateProduct(

            Long id,
            ProductRequest request

    ) {

        Product product =
                findProductById(id);


        product.setProductName(
                request.getProductName()
        );


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


    @Override
    public void deleteProduct(Long id) {

        Product product =
                findProductById(id);

        productRepository.delete(product);
    }


    private Product findProductById(Long id) {

        return productRepository

                .findById(id)

                .orElseThrow(

                        () -> new ResourceNotFoundException(

                                "Product not found with id: " + id

                        )

                );
    }


    private String getCurrentUsername() {

        Authentication authentication =

                SecurityContextHolder

                        .getContext()

                        .getAuthentication();


        if (authentication == null
                || !authentication.isAuthenticated()) {

            return "SYSTEM";
        }


        return authentication.getName();
    }


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

                .id(product.getId())

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