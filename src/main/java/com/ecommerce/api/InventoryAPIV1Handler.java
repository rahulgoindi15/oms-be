package com.ecommerce.api;

import com.ecommerce.controller.InventoryController;
import com.ecommerce.exceptions.ValidationException;
import com.ecommerce.middleware.API;
import com.ecommerce.model.Inventory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory/products")
public class InventoryAPIV1Handler {
    private final InventoryController inventoryController;

    public InventoryAPIV1Handler(InventoryController inventoryController) {
        this.inventoryController = inventoryController;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<API> getInventory(@PathVariable UUID productId) {
        Inventory inventory = inventoryController.getInventory(productId);
        return API.setSuccess(inventory);
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<API> updateInventory(@PathVariable UUID productId, @RequestBody Inventory inventoryRequest) {
        if(inventoryRequest.getStock()<0){
            throw new ValidationException("Stock amount can't be less than 0");
        }
        Inventory inventory = inventoryController.updateInventory(productId, inventoryRequest.getStock());
        return API.setSuccess(inventory);
    }
    @PutMapping("/{productId}")
    public ResponseEntity<API> addStock(@PathVariable UUID productId, @RequestParam Integer stock) {
        if(stock<0){
            throw new ValidationException("Stock amount can't be less than 0");
        }
        Inventory inventory = inventoryController.addStock(productId, stock);
        return API.setSuccess(inventory);
    }
}
