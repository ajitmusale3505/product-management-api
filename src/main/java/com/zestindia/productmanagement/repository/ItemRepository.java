package com.zestindia.productmanagement.repository;

import com.zestindia.productmanagement.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByProductId(Long productId);

}