package com.ecommerce.exceptions;

import com.ecommerce.constants.ErrorMessage;

public class InsufficientStockException extends BadRequestException {
    public InsufficientStockException() {
        super(ErrorMessage.INSUFFICIENT_STOCK);
    }
}
