package com.zestindia.productmanagement.service;

import com.zestindia.productmanagement.dto.request.ItemRequest;
import com.zestindia.productmanagement.dto.response.ItemResponse;

import java.util.List;

public interface ItemService {

    ItemResponse createItem(
            Long productId,
            ItemRequest request
    );

    List<ItemResponse> getItemsByProductId(Long productId);
}