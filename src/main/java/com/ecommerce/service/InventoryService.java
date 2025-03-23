package com.ecommerce.service;

import com.ecommerce.model.Inventory;

import java.util.UUID;

public interface InventoryService {
    Inventory getInventory(UUID productId);

    Inventory updateInventory(UUID productId, int stock);

    Inventory addStock(UUID productId, Integer stock);
}
