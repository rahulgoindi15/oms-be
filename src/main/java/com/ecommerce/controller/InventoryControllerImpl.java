package com.ecommerce.controller;

import com.ecommerce.model.Inventory;
import com.ecommerce.service.InventoryService;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class InventoryControllerImpl implements InventoryController {
    private final InventoryService inventoryService;

    public InventoryControllerImpl(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Override
    public Inventory getInventory(UUID productId) {
        return inventoryService.getInventory(productId);
    }

    @Override
    public Inventory updateInventory(UUID productId, int stock) {
        return inventoryService.updateInventory(productId, stock);
    }

    @Override
    public Inventory addStock(UUID productId, Integer stock) {
        return inventoryService.addStock(productId, stock);
    }
}
