package com.ecommerce.infra.config.redis.beans;

import com.ecommerce.infra.config.redis.config.RedisClusterConfig;
import com.ecommerce.infra.config.redis.config.RedisConfig;
import com.ecommerce.infra.config.service.ServiceConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.stereotype.Component;

@Component
public class RedisFactory {
    private final RedisConfig redisConfig;
    private final RedisClusterConfig redisClusterConfig;
    private final ServiceConfig serviceConfig;

    public RedisFactory(RedisConfig redisConfig, ServiceConfig serviceConfig, RedisClusterConfig redisClusterConfig) {
        this.redisConfig = redisConfig;
        this.serviceConfig = serviceConfig;
        this.redisClusterConfig = redisClusterConfig;
    }

    @Bean
    public JedisConnectionFactory connectionFactory() {
        JedisConnectionFactory jedisConnectionFactory;
        if (serviceConfig.isClusterMode()) {
            RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(redisClusterConfig.getHosts());
            jedisConnectionFactory = new JedisConnectionFactory(redisClusterConfiguration);
        } else {
            RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
            configuration.setHostName(redisConfig.getHost());
            configuration.setPort(redisConfig.getPort());
            jedisConnectionFactory = new JedisConnectionFactory(configuration);
        }
        return jedisConnectionFactory;
    }

    @Bean
    public RedisTemplate<String, Object> template(JedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public static ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }


    @Bean
    @Primary
    public Jackson2ObjectMapperBuilder configureObjectMapper() {
        return new Jackson2ObjectMapperBuilder()
                .modulesToInstall(JavaTimeModule.class);
    }
}