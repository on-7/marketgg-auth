package com.nhnacademy.marketgg.auth.config;

import com.nhnacademy.marketgg.auth.jwt.RefreshToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 기본 설정을 담당합니다.
 *
 * @version 1.0.0
 */

@Configuration
public class RedisConfig {

    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private int port;

    @Value("${redis.password}")
    private String password;

    @Value("${redis.database}")
    private int database;

    /**
     * Redis 연결과 관련된 설정을 하는 RedisConnectionFactory 를 스프링 빈으로 등록한다.
     * key-value 형 데이터베이스를 사용하여 프로젝트를 데이터베이스에 연결하도록 지원하는 팩토리다.
     *
     * @return Thread-safe 한 Lettuce 기반의 커넥션 팩토리 (LettuceConnectionFactory)
     * @since 1.0.0
     * @see <a href="https://lettuce.io/core/release/api">Lettuce 6.x Documentation</a>
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();

        configuration.setHostName(host);
        configuration.setPort(port);
        configuration.setPassword(password);
        configuration.setDatabase(database);

        return new LettuceConnectionFactory(configuration);
    }

    /**
     * Redis 서버에 명령어를 수행하기 위한 높은 수준의 추상화를 제공하는 클래스인 RedisTemplate 을 스프링 빈으로 등록합니다.
     *
     * @param redisConnectionFactory - 스프링 빈으로 등록된 RedisConnectionFactory
     * @return key-value 구조의 RedisTemplate
     * @since 1.0.0
     * @see RedisConfig#redisConnectionFactory
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
        RedisConnectionFactory redisConnectionFactory) {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));

        return redisTemplate;
    }

}
