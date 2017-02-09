package org.gaea.cache;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.gaea.cache.util.GaeaCacheTimeUtils;
import org.gaea.data.dataset.domain.GaeaDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
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
    private RedisTemplate redisTemplate;
    @Autowired(required = false)
    private RedisTemplate jsonRedisTemplate;

    public void put(String key, String value, String gaeaTimeOutStr) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        Long timeOut = GaeaCacheTimeUtils.getTimeMilliSeconds(gaeaTimeOutStr);
        if (timeOut == null) {
            throw new IllegalArgumentException("缓存时间错误。无法转换为毫秒。输入时间：" + gaeaTimeOutStr);
        }
        ops.set(key, value, timeOut, TimeUnit.MILLISECONDS);
    }

    public <T> void put(String key, T value, Class<T> tClass, String gaeaTimeOutStr) {
        // 通过tClass判断，获取合适的redisTemplate
        RedisTemplate<String, T> localRedisTemplate = getRedisTemplate(null, tClass);
        ValueOperations<String, T> ops = localRedisTemplate.opsForValue();
        Long timeOut = GaeaCacheTimeUtils.getTimeMilliSeconds(gaeaTimeOutStr);
        if (timeOut == null) {
            throw new IllegalArgumentException("缓存时间错误。无法转换为毫秒。输入时间：" + gaeaTimeOutStr);
        }
        ops.set(key, value, timeOut, TimeUnit.MILLISECONDS);
    }

    public <T> void put(final String key, Map<String, T> map, Class<T> tClass) {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("key不允许为空。");
        }
        // 通过tClass判断，获取合适的redisTemplate
        RedisTemplate<String, T> localRedisTemplate = getRedisTemplate(tClass, null);
//        // 如果tClass不为空，Redis默认采用json值方式缓存。
//        if(tClass!=null){
//            jsonRedisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<T>(tClass));
//            // 用jsonRedisTemplate去缓存。redis中的值内容为json。
//            localRedisTemplate = jsonRedisTemplate;
//        }
        HashOperations<String, String, T> ops = localRedisTemplate.opsForHash();
        for (String mk : map.keySet()) {
            ops.put(key, mk, map.get(mk));
        }
    }

    public <T> void put(final String key, T[] setArray, Class<T> tClass) {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("key不允许为空。");
        }
        // 通过tClass判断，获取合适的redisTemplate
        RedisTemplate<String, T> localRedisTemplate = getRedisTemplate(tClass, tClass);
        SetOperations<String, T> ops = localRedisTemplate.opsForSet();
        if (ArrayUtils.isNotEmpty(setArray)) {
            ops.add(key, setArray);
        }
    }

    public <T> void putHashValue(String key, String mapKey, T value, Class<T> tClass) {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("key不允许为空。");
        }
        if (StringUtils.isEmpty(mapKey)) {
            throw new IllegalArgumentException("mapKey不允许为空。");
        }
        // 通过tClass判断，获取合适的redisTemplate
        RedisTemplate<String, T> localRedisTemplate = getRedisTemplate(tClass, null);
        HashOperations<String, String, T> ops = localRedisTemplate.opsForHash();
        ops.put(key, mapKey, value);
    }

    public <V> V get(String key, Class<V> vClass) {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("key不允许为空。");
        }
        // 通过vClass判断，获取合适的redisTemplate
        RedisTemplate<String, V> localRedisTemplate = getRedisTemplate(null, vClass);
        ValueOperations<String, V> ops = localRedisTemplate.opsForValue();
        return ops.get(key);
    }

    public <T> T getHashValue(String key, String hashKey, Class<T> tClass) {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("key不允许为空。");
        }
        if (StringUtils.isEmpty(hashKey)) {
            throw new IllegalArgumentException("mapKey不允许为空。");
        }
        // 通过tClass判断，获取合适的redisTemplate
        RedisTemplate<String, T> localRedisTemplate = getRedisTemplate(tClass, null);
        HashOperations<String, String, T> ops = localRedisTemplate.opsForHash();
        T value = ops.get(key, hashKey);
        return value;
    }

    public <T> Map<String, T> getHashAll(String key, Class<T> tClass) {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("key不允许为空。");
        }
        // 通过tClass判断，获取合适的redisTemplate
        RedisTemplate<String, T> localRedisTemplate = getRedisTemplate(tClass, null);
        HashOperations<String, String, T> ops = localRedisTemplate.opsForHash();
        Map<String, T> results = ops.entries(key);
        return results;
    }

    public void delete(String key) {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("key不允许为空。");
        }
        redisTemplate.delete(key);
    }

    public void cachedByStrategy(GaeaDataSet dataSetDef, List<Map<String, Object>> dataList) {
        // 按策略缓存。还没想好缓存架构。先放一放吧。
    }

    /**
     * 协助获取正确的redisTemplate。<br/>
     * hash value或value的任意一个class有定义，默认可以用jsonRedisTemplate.
     * <p>
     * 默认的使用redisTemplate.是使用JdkSerializationRedisSerializer.
     * </p>
     * <p>
     * 如果tClass不为空,则可以用jsonRedisTemplate和Jackson2JsonRedisSerializer.
     * </p>
     *
     * @param hashValueClass
     * @param <T>
     * @return
     */
    private <T> RedisTemplate<String, T> getRedisTemplate(Class<T> hashValueClass, Class<T> valueClass) {
        // 默认使用redisTemplate。如果有tClass，则可以用json的redisTemplate.
        RedisTemplate<String, T> localRedisTemplate = redisTemplate;
        // 如果tClass不为空，Redis默认采用json值方式缓存。
        if (hashValueClass != null) {
            jsonRedisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<T>(hashValueClass));
        }
        if (valueClass != null) {
            jsonRedisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<T>(valueClass));
        }
        // hash value或value的任意一个class有定义，默认可以用jsonRedisTemplate
        if (hashValueClass != null || valueClass != null) {
            // 用jsonRedisTemplate去缓存。redis中的值内容为json。
            localRedisTemplate = jsonRedisTemplate;
        }
        return localRedisTemplate;
    }
}
