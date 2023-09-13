package com.turing.forseason.global.redis;

import com.turing.forseason.global.errorException.CustomException;
import com.turing.forseason.global.errorException.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    public void setValueWithTTL(String key, Object value, long timeout, TimeUnit unit) {
        // <key,value>를 TTL 둬서 redis에 저장
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value, timeout, unit);
    }

    public Object getValue(String key) {
        // key값으로 value 가져오기
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        Object object = valueOperations.get(key);

//        if(object==null) throw new CustomException(ErrorCode.REDIS_NOT_FOUND);

        return object;
    }

    public void deleteValue(String key) {
        // key값으로 value 삭제하기
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.getAndDelete(key);
    }
}
