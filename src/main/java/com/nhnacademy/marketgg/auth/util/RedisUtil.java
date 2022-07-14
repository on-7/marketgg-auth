package com.nhnacademy.marketgg.auth.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisInvalidSubscriptionException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, String> redisTemplate;

    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void set(String key, String value) throws RedisInvalidSubscriptionException {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        if(hasKey(key)) {
            throw new RedisInvalidSubscriptionException("해당 이메일은 메일 재발송 시간이 경과하지 않았습니다."
                    , new IllegalArgumentException());
        }
        valueOperations.set(key, value, 3, TimeUnit.MINUTES);
    }

    public void deleteAuth(String email) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.getAndDelete(email);
    }

    public boolean checkAuth(String email, String authKey) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String authK = valueOperations.getAndDelete(email);
        if (authK.equals(authKey)) {
            return false;
        }
            return true;
        }
}
