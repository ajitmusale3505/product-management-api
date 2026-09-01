package com.zestindia.productmanagement.service.impl;

import com.zestindia.productmanagement.dto.request.ItemRequest;
import com.zestindia.productmanagement.dto.response.ItemResponse;
import com.zestindia.productmanagement.entity.Item;
import com.zestindia.productmanagement.entity.Product;
import com.zestindia.productmanagement.exception.ResourceNotFoundException;
import com.zestindia.productmanagement.repository.ItemRepository;
import com.zestindia.productmanagement.repository.ProductRepository;
import com.zestindia.productmanagement.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final ProductRepository productRepository;

    @Override
    public ItemResponse createItem(
            Long productId,
            ItemRequest request
    ) {

        Product product = productRepository
                .findById(productId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Product not found with id: " + productId
                        )
                );

        Item item = Item.builder()
                .quantity(request.getQuantity())
                .build();

        product.addItem(item);

        Item savedItem = itemRepository.save(item);

        return mapToItemResponse(savedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponse> getItemsByProductId(
            Long productId
    ) {

        if (!productRepository.existsById(productId)) {

            throw new ResourceNotFoundException(
                    "Product not found with id: "
                            + productId
            );
        }

        return itemRepository
                .findByProductId(productId)
                .stream()
                .map(this::mapToItemResponse)
                .toList();
    }

    private ItemResponse mapToItemResponse(
            Item item
    ) {

        return ItemResponse.builder()
                .id(item.getId())
                .quantity(item.getQuantity())
                .build();
    }
}