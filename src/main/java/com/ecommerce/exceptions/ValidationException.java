package com.ecommerce.exceptions;


public class ValidationException extends BadRequestException {
    public ValidationException(String message) {
        super(message);
    }
}
