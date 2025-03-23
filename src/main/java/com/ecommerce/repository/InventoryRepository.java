package com.ecommerce.repository;

import com.ecommerce.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    Optional<Inventory> findByProductId(UUID productId);

    List<Inventory> findByProductIdIn(List<UUID> productIds);
}
