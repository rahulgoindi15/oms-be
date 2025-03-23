package com.ecommerce.sqs.model;

import com.ecommerce.contracts.OrderRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderMessage {
    private UUID orderId;
    private OrderRequest orderRequest;
}
