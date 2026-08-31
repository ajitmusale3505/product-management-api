package com.zestindia.productmanagement.service;

import com.zestindia.productmanagement.dto.request.ProductRequest;
import com.zestindia.productmanagement.dto.response.ProductResponse;
import org.springframework.data.domain.Page;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    ProductResponse getProductById(Long id);

    Page<ProductResponse> getAllProducts(
            int page,
            int size,
            String sortBy,
            String sortDirection
    );

    ProductResponse updateProduct(
            Long id,
            ProductRequest request
    );

    void deleteProduct(Long id);
}