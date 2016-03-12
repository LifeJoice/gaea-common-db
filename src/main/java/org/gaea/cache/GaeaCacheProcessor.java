package org.gaea.cache;

import org.gaea.data.dataset.domain.GaeaDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Gaea框架的统一的缓存处理器。负责封装指定的第三方缓存系统的处理。
 * Created by iverson on 2016/3/9.
 */
@Service
public class GaeaCacheProcessor {
    private final Logger logger = LoggerFactory.getLogger(GaeaCacheProcessor.class);
    @Autowired(required = false)
    private RedisTemplate<String,String> redisTemplate;

    public void put(String key,String value){
        ValueOperations<String,String> ops = redisTemplate.opsForValue();
        ops.set(key, value);
    }

    public <T> void put(final String key, final Map<String,T> map){
        HashOperations<String,String,T> ops = redisTemplate.opsForHash();
        for(String mk:map.keySet()){
            ops.put(key,mk,map.get(mk));
        }
    }

    public <T> T getHashValue(String rootKey,String hashKey){
        HashOperations<String,String,T> ops = redisTemplate.opsForHash();
        T value = ops.get(rootKey,hashKey);
        return value;
    }
}
