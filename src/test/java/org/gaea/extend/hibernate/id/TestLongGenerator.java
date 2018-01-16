package org.gaea.extend.hibernate.id;

import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by iverson on 2018/1/14.
 */
public class TestLongGenerator {
    private static ConcurrentSkipListSet testSet = new ConcurrentSkipListSet();

    public static void main(String[] args) {

        // 可以给GaeaDateTimeLongIDGenerator硬塞个redisTemplate，实现基于redis的测试。
        // -------------------------------------------------- test

//        JedisPoolConfig poolConfig = new JedisPoolConfig();
//        poolConfig.setMaxTotal(10);
//        poolConfig.setMaxIdle(5);
//        poolConfig.setMinIdle(1);
//        poolConfig.setTestOnBorrow(true);
//        poolConfig.setTestOnReturn(true);
//        poolConfig.setTestWhileIdle(true);
//        poolConfig.setMaxWaitMillis(10 * 1000);
//        JedisConnectionFactory connFactory = new JedisConnectionFactory(poolConfig);
//
//        connFactory.setHostName("localhost");
//        connFactory.setPort(6379);
//        connFactory.setUsePool(true);//使用连接池
//        connFactory.afterPropertiesSet();
//
//        redisTemplate = new RedisTemplate<String, Long>();
//
//        redisTemplate.setConnectionFactory(connFactory);
//        redisTemplate.setKeySerializer(new StringRedisSerializer());//key的序列化适配器
//        redisTemplate.setValueSerializer(new GenericToStringSerializer<Long>(Long.class));
//
////        redisTemplate.setValueSerializer(new StringRedisSerializer());//value的序列化适配器，也可以自己编写，大部分场景StringRedisSerializer足以满足需求了。
//
//        redisTemplate.afterPropertiesSet();
//        ops = redisTemplate.opsForValue();
        // -------------------------------------------------- test end


        final GaeaDateTimeLongIDGenerator idGenerator = new GaeaDateTimeLongIDGenerator();
        final int threadLoop = 1000;
        int threads = 10;
        for (int i = 0; i < threads; i++) {
            new Thread(new LongIdGenerator(idGenerator, threadLoop, testSet, i)).start();
//            new Runnable() {
//                @Override
//                public void run() {
//                    for (int j = 0; j < threadLoop; j++) {
//                        String id = (String) idGenerator.generate(null,null);
//                        if(testSet.contains(id)){
//                            System.out.println("-------------->>> 发现重复id！！ "+id);
//                        }
//                        testSet.add(id);
//                    }
//                    System.out.println(" 线程 "+this.getClass().getName()+" 执行完成。当前id池中的id数为："+testSet.size());
//                }
//            }.run();
        }

    }
}
