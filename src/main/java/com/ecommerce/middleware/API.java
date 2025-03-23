package com.ecommerce.middleware;

import com.ecommerce.constants.ErrorMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;

public class API {

    @JsonProperty("data")
    private Object data;

    @JsonProperty("message")
    private String message;

    public API(Object data, String message) {
        this.data = data;
        this.message = message;
    }

    public static ResponseEntity<API> setSuccess(Object body) {
        return new ResponseEntity<>(new API(body, HttpStatus.OK.getReasonPhrase()), HttpStatus.OK);
    }

    public static ResponseEntity<API> setInternalServerError() {
        return new ResponseEntity<>(new API(null, ErrorMessage.SOMETHING_WENT_WRONG), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static ResponseEntity<API> setResponse(Object body, HttpStatusCode status, String message) {
        return new ResponseEntity<>(new API(body, message), status);
    }

    public static ResponseEntity<API> setResponse(Object body, MultiValueMap<String, String> headers, HttpStatusCode status, String message) {
        return new ResponseEntity<>(new API(body, message), headers, status);
    }

    public static ResponseEntity<API> setSuccessNoBody() {
        return new ResponseEntity<>(new API(new HashMap<>(), HttpStatus.OK.getReasonPhrase()), HttpStatus.OK);
    }

}
