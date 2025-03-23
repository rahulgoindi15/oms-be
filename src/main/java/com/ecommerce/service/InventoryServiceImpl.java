package com.ecommerce.service;

import com.ecommerce.constants.ErrorMessage;
import com.ecommerce.exceptions.ValidationException;
import com.ecommerce.model.Inventory;
import com.ecommerce.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class InventoryServiceImpl implements InventoryService{

    private final InventoryRepository inventoryRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public Inventory getInventory(UUID productId) {
        return inventoryRepository.findByProductId(productId).orElse(null);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Inventory updateInventory(UUID productId, int stock) {
        Inventory inventory = inventoryRepository.findByProductId(productId).orElse(null);

        if (inventory == null) {
            inventory = new Inventory();
            inventory.setProductId(productId);
        }
        inventory.setStock(stock);
        return inventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public Inventory addStock(UUID productId, Integer stock) {
        Inventory inventory = inventoryRepository.findByProductId(productId).orElseThrow(() ->
                new ValidationException(ErrorMessage.INVENTORY_NOT_FOUND));

        inventory.setStock(inventory.getStock() + stock);
        return inventoryRepository.save(inventory);
    }
}
