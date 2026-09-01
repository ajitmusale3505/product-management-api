package com.zestindia.productmanagement.repository;

import com.zestindia.productmanagement.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepository
        extends JpaRepository<Product, Long> {


    Page<Product> findByProductNameContainingIgnoreCase(

            String productName,

            Pageable pageable

    );


    /*
     * Find products where at least one item
     * has quantity greater than or equal to minQuantity.
     */
    @Query(
            value = """
                    SELECT DISTINCT p
                    FROM Product p
                    JOIN p.items i
                    WHERE i.quantity >= :minQuantity
                    """,

            countQuery = """
                    SELECT COUNT(DISTINCT p)
                    FROM Product p
                    JOIN p.items i
                    WHERE i.quantity >= :minQuantity
                    """
    )
    Page<Product> findProductsByMinimumQuantity(

            @Param("minQuantity")
            Integer minQuantity,

            Pageable pageable

    );


    /*
     * Find products where at least one item
     * has quantity between minQuantity and maxQuantity.
     */
    @Query(
            value = """
                    SELECT DISTINCT p
                    FROM Product p
                    JOIN p.items i
                    WHERE i.quantity >= :minQuantity
                    AND i.quantity <= :maxQuantity
                    """,

            countQuery = """
                    SELECT COUNT(DISTINCT p)
                    FROM Product p
                    JOIN p.items i
                    WHERE i.quantity >= :minQuantity
                    AND i.quantity <= :maxQuantity
                    """
    )
    Page<Product> findProductsByQuantityRange(

            @Param("minQuantity")
            Integer minQuantity,

            @Param("maxQuantity")
            Integer maxQuantity,

            Pageable pageable

    );

}