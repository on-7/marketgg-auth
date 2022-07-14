package com.nhnacademy.marketgg.auth.util;

import com.nhnacademy.marketgg.auth.exception.EmailOverlapException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtils {

    private final RedisTemplate<String, String> redisTemplate;

    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void set(String key, String value) throws EmailOverlapException {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        if(hasKey(key)) {
            throw new EmailOverlapException(key, "해당 이메일은 중복입니다.");
        }
        valueOperations.set(key, value, 5, TimeUnit.MINUTES);
    }

    public void deleteAuth(String email) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.getAndDelete(email);
    }

    public boolean checkAuth(String email, String authKey) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String authK = valueOperations.getAndDelete(email);
        if (authK.equals(authKey)) {
            return true;
        }
        return false;
    }
}
