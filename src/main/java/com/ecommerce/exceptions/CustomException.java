package com.ecommerce.exceptions;


import org.springframework.http.HttpStatusCode;

public class CustomException extends RuntimeException{
    private final HttpStatusCode code;

    public CustomException(String message, HttpStatusCode code) {
        super(message);
        this.code = code;
    }

    public HttpStatusCode getStatusCode() {
        return code;
    }
}
