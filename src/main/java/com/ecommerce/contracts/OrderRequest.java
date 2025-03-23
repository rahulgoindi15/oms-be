package com.ecommerce.contracts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    @JsonProperty("items")
    private List<OrderItemRequest> items;
}

