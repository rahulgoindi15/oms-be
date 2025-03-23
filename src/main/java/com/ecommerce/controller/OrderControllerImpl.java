package com.ecommerce.controller;

import com.ecommerce.constants.OrderStatus;
import com.ecommerce.contracts.OrderRequest;
import com.ecommerce.contracts.OrderResponse;
import com.ecommerce.exceptions.InsufficientStockException;
import com.ecommerce.service.OrderService;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
public class OrderControllerImpl implements OrderController{
    private final OrderService orderService;

    public OrderControllerImpl(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public OrderResponse placeOrder(OrderRequest request) throws InsufficientStockException {
        return orderService.placeOrder(request);
    }

    @Override
    public void updateOrderStatus(UUID orderId, OrderStatus status) {
        orderService.updateOrderStatus(orderId, status);
    }

    @Override
    public OrderResponse getOrderById(UUID id) {
        return orderService.getOrderById(id);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }
}
