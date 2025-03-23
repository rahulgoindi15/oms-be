package com.ecommerce.api;

import com.ecommerce.constants.OrderStatus;
import com.ecommerce.contracts.OrderRequest;
import com.ecommerce.contracts.OrderResponse;
import com.ecommerce.controller.OrderController;
import com.ecommerce.exceptions.InsufficientStockException;
import com.ecommerce.exceptions.ValidationException;
import com.ecommerce.middleware.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrdersAPIV1Handler {

    private static final Logger log = LoggerFactory.getLogger(OrdersAPIV1Handler.class);
    private final OrderController orderController;

    public OrdersAPIV1Handler(OrderController orderController) {
        this.orderController = orderController;
    }

    @GetMapping("/{id}")
    public ResponseEntity<API> getOrderById(@PathVariable UUID id) {
        OrderResponse order = orderController.getOrderById(id);
        return API.setSuccess(order);
    }

    @GetMapping
    public ResponseEntity<API> getAllOrders() {//todo add pagination, cache, sort and filters by status and date
        return API.setSuccess(orderController.getAllOrders());
    }

    @PostMapping
    public ResponseEntity<API> placeOrder(@RequestBody OrderRequest request) throws InsufficientStockException {
        if (request.getItems().stream().anyMatch(item -> item.getQuantity() < 0)) {
            throw new ValidationException("Stock amount can't be less than 0");
        }

        return API.setSuccess(orderController.placeOrder(request));
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<API> updateOrderStatus(@PathVariable UUID orderId, @RequestBody Map<String, String> requestBody) {
        OrderStatus status = null;
        try {
            status = OrderStatus.valueOf(requestBody.get("status"));
        } catch (Exception e) {
            log.error("error occurred while parsing order status {}", e.getMessage());
            throw new ValidationException("Invalid Order Status!");
        }
        orderController.updateOrderStatus(orderId, status);
        return API.setSuccess("Order status updated");
    }
}
