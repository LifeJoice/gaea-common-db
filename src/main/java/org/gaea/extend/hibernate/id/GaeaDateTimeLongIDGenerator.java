package org.gaea.extend.hibernate.id;

import org.apache.commons.lang3.StringUtils;
import org.gaea.cache.GaeaCacheOperator;
import org.gaea.config.SystemProperties;
import org.gaea.exception.SysInitException;
import org.gaea.util.SpringContextUtils;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.type.Type;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 这是一个自定义的ID生成器。<p/>
 * <p><b>Hibernate应该是在服务器启动的时候，会为每一个entity（有用到GaeaDateTimeLongIDGenerator）生成一个对应实例，负责id生成。所以应该是单例模式。</b></p>
 * <p/>
 * 依赖Hibernate。因为发现JPA和Spring Data貌似都没有自定义的ID生成器。<p/>
 * 规则：<br/>
 * 以年月日时分+一定位数的顺序数为ID。默认是4位顺序数。<br/>
 * 输出ID格式：18011209019999
 * --------------------------------------------------------------------------------------------------------- >>>> 作废
 * <p style='text-decoration:line-through'>
 * 以年月日时分+一定位数的随机数为ID。默认是5位随机数，从26个字母+数字中随机抽取。不含特殊字符。<br/>
 * 因为考虑到特殊字符中的\等在java中是有特殊定义，单引号、双引号在js也有特殊含义。避免程序以后发生不可预测的错误。宁愿多一位随机数。<br/>
 * 输出ID格式：1601301620aXe54
 * </p>
 * --------------------------------------------------------------------------------------------------------- >>>> 作废
 * <p>
 * <b>关于顺序号的控制</b>
 * <ul>
 * <li>单例模式:每个表一个实例</li>
 * <p>
 * 实例中,以一个同步变量记录顺序号. 当到底峰值的时候, 重置起始值(一般是0). 因为有时间做前缀, 在一定时间内出现重复的可能性不高. 但在分布式环境会有问题.
 * </p>
 * <li>基于Redis作为顺序号生成器</li>
 * <p>
 * 每次都从Redis获取顺序号.当到底峰值的时候, 重置起始值(一般是0). 因为有时间做前缀, 在一定时间内出现重复的可能性不高.
 * </p>
 * </ul>
 * </p>
 * <p>copy from GaeaDateTimeIDGenerator</p>
 * <p>为了某些数据库历史原因，id字段是long类型的。</p>
 * Created by iverson on 2017年8月15日14:30:28
 */
public class GaeaDateTimeLongIDGenerator implements IdentifierGenerator, Configurable {
    private final Logger logger = LoggerFactory.getLogger(GaeaDateTimeLongIDGenerator.class);
    private Integer randomLength = null;    // ID中随机生成数的位数
    /* 默认生成序列（不含日期部分）的长度 */
    private static final Integer DEFAULT_LENGTH = 4;
    /* 序列部分的起始数值. */
    private final int START_NUM = 0;
    /* 单机版的序列生成器. */
    private AtomicLong sequence = new AtomicLong(START_NUM);
    /* 如果是REDIS作为序列服务器,则需要gaeaCacheOperator */
    private GaeaCacheOperator gaeaCacheOperator;
    /* 获取系统配置的日期前缀的格式 */
    private static final String PROP_KEY_DATETIME_FORMAT = "system.long_id_generator.datetime_format";
    /* 获取系统配置的默认序列长度 */
    private static final String PROP_KEY_SEQUENCE_LENGTH = "system.long_id_generator.sequence_length";
    /* 全局的redis计数器的key */
    private static final String PROP_KEY_REDIS_COUNTER_KEY = "system.long_id_generator.global.redis_key";
    /* Redis对应序列的key */
    private final String REDIS_COUNTER_KEY;
    /* 序列生成日期部分的格式. */
    private final String DATETIME_FORMAT;
    /* 序列的长度. */
    private final int SEQUENCE_LENGTH;
    /* 递增步长 */
    private int INCREASE_STEP = 1;

    /**
     * 已经初始化的序列的本地存储.<b style='color: red'>本地准同步计数器。</b><br/>
     * 这个对于REDIS生成序列有用. 尝试本地有一个计数器和REDIS同步.<p/>
     * <b>注意:<br/>
     * 这个不需要严格和REDIS同步。因为在初始化的时候需要加同步锁，为了避免每次生成序列都要判断是否初始化，然后又加上同步锁，所以用一个本地的准同步计数器来规避多数的加同步锁的场景。
     * </b>
     */
    private AtomicLong initiatedSequence = new AtomicLong(START_NUM);

    public GaeaDateTimeLongIDGenerator() {
        // 初始化序列的日期部分格式
        if (StringUtils.isNotEmpty(SystemProperties.get(PROP_KEY_DATETIME_FORMAT))) {
            DATETIME_FORMAT = SystemProperties.get(PROP_KEY_DATETIME_FORMAT);
        } else {
            DATETIME_FORMAT = "yyMMddHHmm";
        }
        // 初始化序列的长度
        if (StringUtils.isNotEmpty(SystemProperties.get(PROP_KEY_SEQUENCE_LENGTH))) {
            SEQUENCE_LENGTH = SystemProperties.getInteger(PROP_KEY_SEQUENCE_LENGTH);
        } else {
            SEQUENCE_LENGTH = DEFAULT_LENGTH;
        }
        // 初始化REDIS计数器的key
        if (StringUtils.isNotEmpty(SystemProperties.get(PROP_KEY_REDIS_COUNTER_KEY))) {
            REDIS_COUNTER_KEY = SystemProperties.get(PROP_KEY_REDIS_COUNTER_KEY);
        } else {
            REDIS_COUNTER_KEY = "";
        }
    }

    /**
     * Hibernate应该是在服务器启动的时候，会为每一个entity（有用到GaeaDateTimeLongIDGenerator）生成一个对应实例，负责id生成。
     *
     * @param type
     * @param properties
     * @param dialect
     * @throws MappingException
     */
    public void configure(Type type, Properties properties, Dialect dialect) throws MappingException {
        String length = properties.getProperty("randomLength");
        if (randomLength == null) {
            randomLength = (StringUtils.isEmpty(length) ? DEFAULT_LENGTH : Integer.parseInt(length));
        }
    }

    /**
     * 生成随机数
     *
     * @param sessionImplementor
     * @param o
     * @return
     * @throws HibernateException
     */
    public Serializable generate(SessionImplementor sessionImplementor, Object o) throws HibernateException {
        // 在调用的时候尝试获取gaeaCacheOperator。因为Hibernate初始化IdentifierGenerator的时候，Spring容器还没初始化完成。所以无法在构造方法初始化。
        try {
            if (gaeaCacheOperator == null && SpringContextUtils.getBean(GaeaCacheOperator.class) != null) {
                gaeaCacheOperator = SpringContextUtils.getBean(GaeaCacheOperator.class);
            }
        } catch (SysInitException e) {
            logger.error("初始化gaea long id generator失败！无法获取Spring的GaeaCacheOperator对象。Id generator将采用单实例模式进行id生成。  " + e.getMessage(), e);
        }
        // 序列的日期部分
        long dateTimePrefix = Long.parseLong(new DateTime().toString(DATETIME_FORMAT));
        // 序列sequence部分的长度(例如: 10000表示)
        long subSequenceDecimal = (long) Math.pow(10, SEQUENCE_LENGTH); // 除了时间外的序列的位数
        // 由于序列基于时间. 根据时间得出基数. 例如: 18011209010000
        long beginByTime = dateTimePrefix * subSequenceDecimal;
        // 由于序列基于时间. 根据时间得出最大数. 例如: 18011209019999
        long maxCountByTime = (dateTimePrefix + 1) * subSequenceDecimal - 1; // 时间进一再整体减一。例如：18011209019999
        /**
         * 警戒线.
         * 由于时间是基数, 如果序列sequence用完了, 就要轮回. 这个时候需要重置操作, 就需要加同步锁. 为了避免频繁加同步锁,所以只有当序列达到一定程度(警戒线)才开始加锁做判断.
         * 默认警戒线比例: 9/10
         */
        long warningCount = Long.parseLong(String.valueOf(dateTimePrefix) + String.valueOf(subSequenceDecimal * 9 / 10));
        /**
         * 这里用了两个同步控制。首先initiatedSequence是原子操作，确保读取的时候最新。但这里无法保证同步，只是为了避免每次进入init产生的锁消耗。
         * 其次进入init的时候，就会必然的加上锁。但因为beginByTime > initiatedSequence.get()并不是同步的，所以在init中还需要再判断一次。
         */
        // 重置sequence. 如果时间跳过一个区间，例如从10:20:20 -> 10:20:21
        if (beginByTime > initiatedSequence.get()) {
            init(beginByTime);
        }
        long generateId;
        if (get() < warningCount) {
            generateId = getAndAdd(1);
        } else {
            logger.trace("id: " + get() + "  进入同步方法。");
            // 进入警戒线，进行同步控制. 因为超了警戒线需要归零操作。
            synchronized (this) {
                generateId = getAndAdd(INCREASE_STEP);
                // 如果达到最大值，归0
                if (generateId > maxCountByTime) {
                    set(beginByTime);
                    generateId = getAndAdd(INCREASE_STEP);
                }
            }
        }
        return generateId;
    }

    /**
     * 必须同步调用。避免初始化互相覆盖。
     * 进入init的时候，就会必然的加上锁。但因为beginByTime > initiatedSequence.get()并不是同步的，所以在init中还需要再判断一次。
     *
     * @param beginByTime
     */
    private synchronized void init(long beginByTime) {
        if (get() == null || beginByTime > get()) {
            set(beginByTime);
            // 这个是粗的过滤
            initiatedSequence.set(beginByTime);
        }
    }

    /**
     * 这个是递增的核心方法。必须确保原子操作。
     *
     * @param delta
     * @return
     */
    private long getAndAdd(long delta) {
        if (StringUtils.isNotEmpty(REDIS_COUNTER_KEY) && gaeaCacheOperator != null) {
            long sequence = gaeaCacheOperator.increment(REDIS_COUNTER_KEY, delta);
            // 因为redis返回的是增加后的值，所以减一
            return sequence - 1;
        } else {
            return sequence.getAndAdd(delta);
        }
    }

    /**
     * @return 这里必须返回对象Long。对于Redis而言，获取不到就是null。
     */
    private Long get() {
        if (StringUtils.isNotEmpty(REDIS_COUNTER_KEY) && gaeaCacheOperator != null) {
            return gaeaCacheOperator.get(REDIS_COUNTER_KEY, Long.class);
        } else {
            return sequence.get();
        }
    }

    private void set(long value) {
        if (StringUtils.isNotEmpty(REDIS_COUNTER_KEY) && gaeaCacheOperator != null) {
            gaeaCacheOperator.put(REDIS_COUNTER_KEY, value, Long.class);
        } else {
            sequence.set(value);
        }
    }
}
