package com.ecommerce.infra.config.aws;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties("aws")
@AllArgsConstructor
@Getter
public class AWSConfig {

    private String region;
}
