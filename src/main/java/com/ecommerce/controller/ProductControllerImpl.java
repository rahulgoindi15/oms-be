package com.ecommerce.controller;

import com.ecommerce.contracts.ProductRequest;
import com.ecommerce.contracts.ProductResponse;
import com.ecommerce.service.ProductService;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
public class ProductControllerImpl implements ProductController {

    private final ProductService productService;

    public ProductControllerImpl(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public UUID addProduct(ProductRequest request) {
        return productService.addProduct(request);
    }

    @Override
    public List<UUID> addProductsBulk(List<ProductRequest> requests) {
        return productService.addProductsBulk(requests);
    }

    @Override
    public ProductResponse getProductById(UUID productId) {
        return productService.getProductById(productId);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }
}
