package com.nhnacademy.marketgg.auth.config;

import com.nhnacademy.marketgg.auth.session.redis.RedisSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 설정을 담당한다.
 *
 * @author 윤동열
 */
@Profile("redis")
@Configuration
public class RedisSessionConfig {

    @Value("${spring.redis.port}")
    Integer port;

    @Value("${spring.redis.host}")
    String host;

    @Value("${spring.redis.password}")
    String password;

    @Value("${spring.redis.database}")
    Integer database;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {

        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);

        configuration.setPassword(password);
        configuration.setDatabase(database);

        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    public RedisTemplate<String, RedisSession> redisTemplate(RedisConnectionFactory redisConnectionFactory) {

        RedisTemplate<String, RedisSession> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(RedisSession.class));

        return redisTemplate;
    }

}
