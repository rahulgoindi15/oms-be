package com.ecommerce.service;

import com.ecommerce.contracts.ProductRequest;
import com.ecommerce.contracts.ProductResponse;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    UUID addProduct(ProductRequest request);
    List<UUID> addProductsBulk(List<ProductRequest> requests);
    ProductResponse getProductById(UUID productId);

    List<ProductResponse> getAllProducts();
}
