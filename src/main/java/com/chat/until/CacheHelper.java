package com.chat.until;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Set;

@Component
@Slf4j
public class CacheHelper {

    /*缓存失效时间60秒*/
    public static final Duration TTL = Duration.ofSeconds(60);

    @Autowired
    private StringRedisTemplate redis;

    @Autowired
    private ObjectMapper objectMapper;   // 和接口返回用的是同一个，序列化格式不会出现两套

/*
* 读取缓存，使用自动序列化*/
    public <T> T get(String key, Class<T> type) {
        try {
            String json = redis.opsForValue().get(key);
            if (json == null || json.isEmpty()) {
                return null;
            }
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            log.warn("读缓存失败，回落到数据库：{} -> {}", key, e.getMessage());
            return null;
        }
    }


    /*
    *
    * 建立缓存*/
    public void put(String key, Object value) {
        try {
            redis.opsForValue().set(key, objectMapper.writeValueAsString(value), TTL);
        } catch (Exception e) {
            log.warn("写缓存失败（不影响本次返回）：{} -> {}", key, e.getMessage());
        }
    }


    /*
    * 清除缓存*/
    public void evict(String prefix) {
        try {
            Set<String> keys = redis.keys(prefix + "*");
            if (keys != null && !keys.isEmpty()) {
                redis.delete(keys);
            }
        } catch (Exception e) {
            log.warn("清缓存失败（旧数据最多再活 {} 秒）：{} -> {}", TTL.getSeconds(), prefix, e.getMessage());
        }
    }
}
