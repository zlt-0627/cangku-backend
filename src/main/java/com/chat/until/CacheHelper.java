package com.chat.until;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Set;

/**
 * 旁路缓存（Cache-Aside）的小工具。
 * <p>
 * 用法就三步：
 * <pre>
 *   RoleVo cached = cache.get(key, RoleVo.class);   // 1. 先查缓存
 *   if (cached != null) return cached;              // 2. 命中直接返回
 *   ... 查数据库 ...
 *   cache.put(key, roleVo);                         // 3. 查完写回缓存
 * </pre>
 * 数据被改的时候调 {@code evict("cache:goods:")} 把这一组缓存删掉，下次读自然重建。
 * <p>
 * 三个方法都吞异常：Redis 挂了就当没有缓存，业务照样走数据库，不会把接口拖死。
 */
@Component
@Slf4j
public class CacheHelper {

    /** 缓存存活时间。列表页这种数据 60 秒足够了，想改就改这里 */
    public static final Duration TTL = Duration.ofSeconds(60);

    @Autowired
    private StringRedisTemplate redis;

    @Autowired
    private ObjectMapper objectMapper;   // 和接口返回用的是同一个，序列化格式不会出现两套

    /** 取缓存，没有或者出错都返回 null（调用方就当没命中，去查库） */
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

    /** 写缓存，带 TTL */
    public void put(String key, Object value) {
        try {
            redis.opsForValue().set(key, objectMapper.writeValueAsString(value), TTL);
        } catch (Exception e) {
            log.warn("写缓存失败（不影响本次返回）：{} -> {}", key, e.getMessage());
        }
    }

    /**
     * 按前缀清缓存，比如 evict("cache:goods:") 会把所有分页的商品缓存删掉。
     * keys 命令会遍历整个库，数据量大的时候要换成 SCAN，这个单体项目用不着。
     */
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
