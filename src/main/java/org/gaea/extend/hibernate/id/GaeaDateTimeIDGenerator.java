package org.gaea.extend.hibernate.id;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Date;
import java.util.Properties;

/**
 * 这是一个自定义的ID生成器。<p/>
 * 依赖Hibernate。因为发现JPA和Spring Data貌似都没有自定义的ID生成器。<p/>
 * 规则：<br/>
 * 以年月日时分+一定位数的随机数为ID。默认是5位随机数，从26个字母+数字中随机抽取。不含特殊字符。<br/>
 * 因为考虑到特殊字符中的\等在java中是有特殊定义，单引号、双引号在js也有特殊含义。避免程序以后发生不可预测的错误。宁愿多一位随机数。
 * <p>输出ID格式：1601301620aXe54</p>
 * Created by iverson on 2016/1/30.
 */
public class GaeaDateTimeIDGenerator implements IdentifierGenerator, Configurable {
    private final Logger logger = LoggerFactory.getLogger(GaeaDateTimeIDGenerator.class);
    private Integer randomLength = null;    // ID中随机生成数的位数
    private static final Integer DEFAULT_RANDOM_LENGTH = 5;

    public void configure(Type type, Properties properties, Dialect dialect) throws MappingException {
        String length = properties.getProperty("randomLength");
        if (randomLength == null) {
            randomLength = (StringUtils.isEmpty(length) ? DEFAULT_RANDOM_LENGTH : Integer.parseInt(length));
        }
    }

    /**
     * 生成随机数
     * @param sessionImplementor
     * @param o
     * @return
     * @throws HibernateException
     */
    public Serializable generate(SessionImplementor sessionImplementor, Object o) throws HibernateException {
        synchronized (this) {
            if (randomLength != null && randomLength > 0) {
                // 生成ID以年月日时分开始。不到秒。
                String dateTime = DateFormatUtils.format(new Date(), "yyMMddHHmm");
                // 生成随机码，从ASCII码的第33位到126位作为随机字符池。ASCII 32位是空格，不能作为随机字符池。
                String randomNum = RandomStringUtils.random(5, false, false);
                return dateTime + randomNum;
            }
            return null;
        }
    }
}
