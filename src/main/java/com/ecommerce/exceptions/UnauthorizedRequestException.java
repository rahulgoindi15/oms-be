package com.ecommerce.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedRequestException extends CustomException{

    public UnauthorizedRequestException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
