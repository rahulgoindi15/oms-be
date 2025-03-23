package com.ecommerce.controller;

import com.ecommerce.constants.OrderStatus;
import com.ecommerce.contracts.OrderRequest;
import com.ecommerce.contracts.OrderResponse;
import com.ecommerce.exceptions.InsufficientStockException;

import java.util.List;
import java.util.UUID;

public interface OrderController {

    OrderResponse placeOrder(OrderRequest request) throws InsufficientStockException;
    void updateOrderStatus(UUID orderId, OrderStatus status);

    OrderResponse getOrderById(UUID id);

    List<OrderResponse> getAllOrders();
}