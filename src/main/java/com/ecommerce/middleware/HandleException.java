package com.ecommerce.middleware;

import com.ecommerce.exceptions.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.stream.Collectors;

@ControllerAdvice
public class HandleException {

    @ExceptionHandler()
    public ResponseEntity<API> handleException(HttpServletRequest request, Exception ex) {
        ex.printStackTrace();
        if (ex instanceof CustomException customException) {
            return API.setResponse(new HashMap<>(), customException.getStatusCode(), customException.getMessage());
        }
        else if (ex instanceof MethodArgumentTypeMismatchException matmEx) {
            String paramName = matmEx.getName();
            String paramType = matmEx.getRequiredType() != null ? matmEx.getRequiredType().getSimpleName() : "unknown";
            String errorMessage = String.format("Invalid value for parameter '%s'. Expected type: %s", paramName, paramType);
            return API.setResponse(new HashMap<>(), HttpStatus.BAD_REQUEST, errorMessage);
        }
        else if (ex instanceof HttpMessageNotReadableException) {
            return API.setResponse(new HashMap<>(), HttpStatus.BAD_REQUEST, "Malformed JSON request body.");
        }

        // Handle missing request parameters
        else if (ex instanceof MissingServletRequestParameterException missingParamEx) {
            String errorMessage = String.format("Missing required parameter: %s", missingParamEx.getParameterName());
            return API.setResponse(new HashMap<>(), HttpStatus.BAD_REQUEST, errorMessage);
        }

        // Handle validation errors (e.g., @Valid or @Validated annotations)
        else if (ex instanceof MethodArgumentNotValidException validationEx) {
            String errorMessage = validationEx.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return API.setResponse(new HashMap<>(), HttpStatus.BAD_REQUEST, "Validation failed: " + errorMessage);
        }

        // Handle constraint violations (e.g., @Size, @NotNull)
        else if (ex instanceof ConstraintViolationException constraintEx) {
            String errorMessage = constraintEx.getConstraintViolations()
                    .stream()
                    .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                    .collect(Collectors.joining(", "));
            return API.setResponse(new HashMap<>(), HttpStatus.BAD_REQUEST, "Constraint violation: " + errorMessage);
        }

        // Handle HTTP method not supported (e.g., using POST instead of GET)
        else if (ex instanceof HttpRequestMethodNotSupportedException methodEx) {
            String errorMessage = "HTTP method not supported: " + methodEx.getMethod();
            return API.setResponse(new HashMap<>(), HttpStatus.METHOD_NOT_ALLOWED, errorMessage);
        }

        // Handle database constraint violations (e.g., duplicate entry, foreign key failure)
        else if (ex instanceof DataIntegrityViolationException dbEx) {
            String errorMessage = "Database error: " + (dbEx.getRootCause() != null ? dbEx.getRootCause().getMessage() : dbEx.getMessage());
            return API.setResponse(new HashMap<>(), HttpStatus.CONFLICT, errorMessage);
        }

        // Default internal server error
        return API.setInternalServerError();
    }
}
