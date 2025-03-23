package com.ecommerce.service;

import com.ecommerce.contracts.ProductRequest;
import com.ecommerce.contracts.ProductResponse;
import com.ecommerce.exceptions.ValidationException;
import com.ecommerce.model.Inventory;
import com.ecommerce.repository.InventoryRepository;
import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final InventoryService inventoryService;
    private final InventoryRepository inventoryRepository;
    private final Logger log = LoggerFactory.getLogger(ProductService.class);

    public ProductServiceImpl(ProductRepository productRepository, InventoryService inventoryService, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryService = inventoryService;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UUID addProduct(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setCreatedAt(ZonedDateTime.now());
        product.setUpdatedAt(ZonedDateTime.now());

        product = productRepository.saveAndFlush(product);
        final Product productFinal = product;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                log.info("Transaction committed. Now updating inventory.");
                inventoryService.updateInventory(productFinal.getId(), 0);
            }
        });

        return product.getId();
    }

    @Override
    @Transactional
    public List<UUID> addProductsBulk(List<ProductRequest> requests) {
        List<Product> products = requests.stream().map(req -> {
            Product product = new Product();
            product.setName(req.getName());
            product.setPrice(req.getPrice());
            product.setDescription(req.getDescription());
            product.setCreatedAt(ZonedDateTime.now());
            product.setUpdatedAt(ZonedDateTime.now());
            return product;
        }).collect(Collectors.toList());

        products = productRepository.saveAllAndFlush(products);
        final List<Product> productsFinal = products;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                log.info("Transaction committed. Now updating inventory.");
                productsFinal.forEach(prod -> inventoryService.updateInventory(prod.getId(), 0));
            }
        });
        return products.stream().map(Product::getId).toList();
    }

    @Override
    public ProductResponse getProductById(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ValidationException("Product not found"));

        // Get stock count from Inventory
        int stock = inventoryRepository.findByProductId(productId)
                .map(Inventory::getStock)
                .orElse(0);

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                stock,
                product.getDescription()
        );
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();

        if(!products.isEmpty()){
            List<Inventory> inventory = inventoryRepository.findByProductIdIn(products.stream().map(Product::getId).toList());
            Map<UUID, Integer> stockCountMap = new HashMap<>();
            if(!inventory.isEmpty()){
                stockCountMap = inventory.stream().collect(Collectors.toMap(Inventory::getProductId, Inventory::getStock));
            }
            List<ProductResponse> responses = new ArrayList<>();
            for(Product product : products){
                responses.add(new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        stockCountMap.getOrDefault(product.getId(), 0),
                        product.getDescription()
                ));
            }
            return responses;
        }
        else return Collections.emptyList();
    }
}
