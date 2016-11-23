package org.gaea.cache;

import org.apache.commons.lang3.StringUtils;
import org.gaea.data.cache.CacheOperator;
import org.gaea.data.dataset.domain.GaeaDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Gaea框架的统一的缓存处理器。负责封装指定的第三方缓存系统的处理。
 * Created by iverson on 2016/3/9.
 */
@Service
public class GaeaCacheOperator implements CacheOperator {
    private final Logger logger = LoggerFactory.getLogger(GaeaCacheOperator.class);
    @Autowired(required = false)
    private RedisTemplate<String, String> redisTemplate;

    public void put(String key, String value, long timeOut, TimeUnit timeUnit) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(key, value, timeOut, timeUnit);
    }

    public <T> void put(final String key, final Map<String, T> map) {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("key不允许为空。");
        }
        HashOperations<String, String, T> ops = redisTemplate.opsForHash();
        for (String mk : map.keySet()) {
            ops.put(key, mk, map.get(mk));
        }
    }

    public <T> void putHashValue(String key, String mapKey, T value) {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("key不允许为空。");
        }
        if (StringUtils.isEmpty(mapKey)) {
            throw new IllegalArgumentException("mapKey不允许为空。");
        }
        HashOperations<String, String, T> ops = redisTemplate.opsForHash();
        ops.put(key, mapKey, value);
    }

    public <T> T getHashValue(String key, String hashKey) {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("key不允许为空。");
        }
        if (StringUtils.isEmpty(hashKey)) {
            throw new IllegalArgumentException("mapKey不允许为空。");
        }
        HashOperations<String, String, T> ops = redisTemplate.opsForHash();
        T value = ops.get(key, hashKey);
        return value;
    }

    public void cachedByStrategy(GaeaDataSet dataSetDef, List<Map<String, Object>> dataList) {
        // 按策略缓存。还没想好缓存架构。先放一放吧。
    }
}
