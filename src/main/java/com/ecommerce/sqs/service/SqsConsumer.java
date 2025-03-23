package com.ecommerce.sqs.service;

import com.ecommerce.constants.OrderStatus;
import com.ecommerce.contracts.OrderRequest;
import com.ecommerce.exceptions.InsufficientStockException;
import com.ecommerce.model.Inventory;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import com.ecommerce.repository.InventoryRepository;
import com.ecommerce.repository.OrderItemRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.service.OrderService;
import com.ecommerce.sqs.model.OrderMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import com.ecommerce.contracts.OrderItemRequest;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SqsConsumer {
    private final OrderService orderService;

    public SqsConsumer(OrderService orderService) {
        this.orderService = orderService;

    }


    @io.awspring.cloud.sqs.annotation.SqsListener("https://sqs.us-east-1.amazonaws.com/897120551968/orders.fifo")
    public void processOrders(OrderMessage orderMessage) {
        try {
            orderService.placeOrderInternal(orderMessage.getOrderId());
        } catch (Exception e) {
            log.error("Order processing failed, retrying...", e);
        }
    }



}
