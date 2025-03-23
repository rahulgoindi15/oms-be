package com.ecommerce.service;

import com.ecommerce.constants.ErrorMessage;
import com.ecommerce.constants.OrderStatus;
import com.ecommerce.contracts.OrderItemRequest;
import com.ecommerce.contracts.OrderRequest;
import com.ecommerce.contracts.OrderResponse;
import com.ecommerce.contracts.OrderItemResponse;
import com.ecommerce.exceptions.CustomException;
import com.ecommerce.exceptions.InsufficientStockException;
import com.ecommerce.exceptions.ValidationException;
import com.ecommerce.model.*;
import com.ecommerce.repository.InventoryRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.OrderItemRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.sqs.model.OrderMessage;
import com.ecommerce.sqs.service.SqsProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final ProductRepository productRepository;

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryRepository inventoryRepository;
    private final SqsProducer sqsProducer;

    public OrderServiceImpl(OrderRepository orderRepository, OrderItemRepository orderItemRepository, InventoryRepository inventoryRepository, SqsProducer sqsProducer,
                            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.inventoryRepository = inventoryRepository;
        this.sqsProducer = sqsProducer;
        this.productRepository = productRepository;
    }

    @Override
    public OrderResponse placeOrder(OrderRequest request) {

        // Generate Order ID
        Order order = Order.builder().status(OrderStatus.DRAFT).build();
        order = orderRepository.save(order);
        Order finalOrder = order;

        List<Product> products = productRepository.findAllById(request.getItems().stream().map(OrderItemRequest::getProductId).toList());
        if(products.isEmpty() || products.size() != request.getItems().size())
            throw new ValidationException(ErrorMessage.PRODUCT_NOT_FOUND);

        List<OrderItem> items = request.getItems().stream().map(item -> new OrderItem(finalOrder, item.getProductId(), item.getQuantity())).toList();
        orderItemRepository.saveAll(items);
        // Push order request to SQS
        try {
            sqsProducer.sendMessage(new OrderMessage(order.getId(), null));
        } catch (Exception e) {
            log.error("Order processing failed due to sqs error: {}", e.getMessage());
            order.setStatus(OrderStatus.FAILED);
            throw new CustomException("Order Processing Failed!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        order.setStatus(OrderStatus.PENDING);
        orderRepository.save(order);

        // Return PENDING response immediately
        return new OrderResponse(order.getId(), OrderStatus.PENDING, Collections.emptyList());
    }

    @Override
    public OrderResponse getOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ValidationException(ErrorMessage.ORDER_NOT_FOUND));

        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

        List<OrderItemResponse> orderItemResponses = orderItems.stream()
                .map(item -> new OrderItemResponse(item.getProductId(), item.getQuantity()))
                .collect(Collectors.toList());

        return new OrderResponse(order.getId(), order.getStatus(), orderItemResponses);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream().map(order -> {
            List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

            List<OrderItemResponse> orderItemResponses = orderItems.stream()
                    .map(item -> new OrderItemResponse(item.getProductId(), item.getQuantity()))
                    .collect(Collectors.toList());

            return new OrderResponse(order.getId(), order.getStatus(), orderItemResponses);
        }).collect(Collectors.toList());
    }

    @Retryable(value = {InsufficientStockException.class, SQLException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2))
    @Transactional
    public void placeOrderInternal(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ValidationException(ErrorMessage.ORDER_NOT_FOUND));
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        List<UUID> productIds = orderItems.stream()
                .map(OrderItem::getProductId)
                .collect(Collectors.toList());

        List<Inventory> inventories = inventoryRepository.findByProductIdIn(productIds);
        Map<UUID, Inventory> inventoryMap = inventories.stream()
                .collect(Collectors.toMap(Inventory::getProductId, inv -> inv));

        for (OrderItem item : orderItems) {
            Inventory inventory = inventoryMap.get(item.getProductId());
            if (inventory == null || inventory.getStock() < item.getQuantity()) {
                throw new InsufficientStockException();
            }
        }

        orderItems.forEach(item -> {
            Inventory inventory = inventoryMap.get(item.getProductId());
            inventory.setStock(inventory.getStock() - item.getQuantity());
        });

        inventoryRepository.saveAll(inventoryMap.values());

        order.setStatus(OrderStatus.CONFIRMED);
        order = orderRepository.save(order);

        log.info("Order {} has been marked as CONFIRMED", orderId);
    }

    @Recover
    public void fallbackMethod(InsufficientStockException e, UUID orderId, OrderRequest request) {
        log.error("Order placement failed after retries : {}", e.getMessage());
        updateStatus(orderId, request, OrderStatus.FAILED);
    }

    @Recover
    public void fallbackMethod(SQLException e, UUID orderId, OrderRequest request) {
        log.error("Database connection failed: {}", e.getMessage());
        updateStatus(orderId, request, OrderStatus.FAILED);

        // Restore inventory
        for (OrderItemRequest item : request.getItems()) {
            Inventory inventory = inventoryRepository.findByProductId(item.getProductId()).orElse(null);
            if (inventory != null) {
                inventory.setStock(inventory.getStock() + item.getQuantity());
                inventoryRepository.save(inventory);
            }
        }
    }


    private void updateStatus(UUID orderId, OrderRequest request, OrderStatus status) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ValidationException(ErrorMessage.ORDER_NOT_FOUND));
        order.setStatus(status);
        order = orderRepository.save(order); // Save order with Failed status

        Order finalOrder = order;
        List<OrderItem> orderItems = request.getItems().stream()
                .map(item -> new OrderItem(finalOrder, item.getProductId(), item.getQuantity()))
                .collect(Collectors.toList());

        orderItemRepository.saveAll(orderItems); // Save order items

        log.info("Order {} has been marked as CANCELED", orderId);
    }

    @Transactional
    @Override
    public void updateOrderStatus(UUID orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ValidationException(ErrorMessage.ORDER_NOT_FOUND));

        if(List.of(OrderStatus.FAILED, OrderStatus.DRAFT).contains(order.getStatus())){
            log.error("Cannot update status for draft or failed orders!");
            throw new ValidationException("Cannot update status for draft or failed orders!");
        }
        order.setStatus(status);
        orderRepository.save(order);
    }
}
