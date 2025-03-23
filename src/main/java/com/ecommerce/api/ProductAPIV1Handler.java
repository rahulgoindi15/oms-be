package com.ecommerce.api;

import com.ecommerce.contracts.ProductRequest;
import com.ecommerce.controller.ProductController;
import com.ecommerce.middleware.API;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductAPIV1Handler {

    private final ProductController productController;

    @PostMapping
    public ResponseEntity<API> addProduct(@RequestBody ProductRequest request) {
        return API.setSuccess(productController.addProduct(request));
    }

    @PostMapping("/bulk")
    public ResponseEntity<API> addProductsBulk(@RequestBody List<ProductRequest> requests) {
        return API.setSuccess(productController.addProductsBulk(requests));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<API> getProductById(@PathVariable UUID productId) {
        return API.setSuccess(productController.getProductById(productId));
    }
    @GetMapping//todo add pagination, filters, sort etc
    public ResponseEntity<API> getAllProducts() {
        return API.setSuccess(productController.getAllProducts());
    }
}
