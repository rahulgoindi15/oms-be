package com.ecommerce.infra.config.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "service")
@AllArgsConstructor
@Getter
public class ServiceConfig {
    private final String cachePrefix ="test";
    private final String cacheMode ="node";

    public boolean isClusterMode(){
        String clusterMode = "cluster";
        return false;
    }

}
