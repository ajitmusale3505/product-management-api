package com.zestindia.productmanagement.controller;

import com.zestindia.productmanagement.dto.request.ItemRequest;
import com.zestindia.productmanagement.dto.request.ProductRequest;
import com.zestindia.productmanagement.dto.response.ItemResponse;
import com.zestindia.productmanagement.dto.response.ProductResponse;
import com.zestindia.productmanagement.dto.response.PagedResponse;
import com.zestindia.productmanagement.service.ItemService;
import com.zestindia.productmanagement.service.ProductService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ItemService itemService;


    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request
    ) {

        ProductResponse response =
                productService.createProduct(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                productService.getProductById(id)
        );
    }


    @GetMapping
    public ResponseEntity<PagedResponse<ProductResponse>> getAllProducts(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "id")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String sortDirection
    ) {

        return ResponseEntity.ok(
                PagedResponse.from(
                        productService.getAllProducts(
                                page,
                                size,
                                sortBy,
                                sortDirection
                        )
                )
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,

            @Valid
            @RequestBody
            ProductRequest request
    ) {

        return ResponseEntity.ok(
                productService.updateProduct(id, request)
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id
    ) {

        productService.deleteProduct(id);

        return ResponseEntity
                .noContent()
                .build();
    }


    @PostMapping("/{productId}/items")
    public ResponseEntity<ItemResponse> createItem(

            @PathVariable Long productId,

            @Valid
            @RequestBody
            ItemRequest request
    ) {

        ItemResponse response =
                itemService.createItem(
                        productId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @GetMapping("/{productId}/items")
    public ResponseEntity<List<ItemResponse>> getItemsByProductId(
            @PathVariable Long productId
    ) {

        return ResponseEntity.ok(
                itemService.getItemsByProductId(productId)
        );
    }
}