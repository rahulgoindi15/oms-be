package com.ecommerce.controller;

import com.ecommerce.model.Inventory;

import java.util.UUID;

public interface InventoryController {
    Inventory getInventory(UUID productId);

    Inventory updateInventory(UUID productId, int stock);

    Inventory addStock(UUID productId, Integer stock);
}
