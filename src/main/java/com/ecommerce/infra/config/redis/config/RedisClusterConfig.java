package com.ecommerce.infra.config.redis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("redis-cluster")
public class RedisClusterConfig {
    private final List<String> hosts;

    public RedisClusterConfig(List<String> hosts) {
        this.hosts = hosts;
    }

    public List<String> getHosts() {
        return hosts;
    }
}
