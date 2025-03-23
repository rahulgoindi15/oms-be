package com.ecommerce.api;

import com.ecommerce.middleware.API;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/health-check")
public class HealthCheck {
    @GetMapping
    public ResponseEntity<API> getHealthCheck() {
        return API.setSuccess("Server is Working");
    }
}
