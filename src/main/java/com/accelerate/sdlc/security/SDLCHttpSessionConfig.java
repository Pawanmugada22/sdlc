package com.accelerate.sdlc.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class SDLCHttpSessionConfig {
    @Bean
    protected JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        return factory;
    }
    @Bean
    public RedisTemplate<String,Object> redisTemplate() {
        final RedisTemplate<String,Object> redisTemplate = new RedisTemplate<String,Object>();
        return redisTemplate;
    }
}
