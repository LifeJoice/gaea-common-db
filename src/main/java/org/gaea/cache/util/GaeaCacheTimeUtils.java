package org.gaea.cache.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 和缓存时间相关的转换。
 * 1ms - 毫秒
 * 1s - 秒
 * 1min - 分
 * 1h - 小时
 * 1d - 天
 * Created by iverson on 2016/12/27.
 */
public class GaeaCacheTimeUtils {
    private static final Logger logger = LoggerFactory.getLogger(GaeaCacheTimeUtils.class);

    public static final String MILLISECOND_SUFFIX = "ms";
    public static final String SECOND_SUFFIX = "s";
    public static final String MINUTE_SUFFIX = "min";
    public static final String HOUR_SUFFIX = "h";
    public static final String DAY_SUFFIX = "d";

    /**
     * 负责把gaea缓存框架的时间字符，转换为毫秒。
     *
     * @param gaeaTime
     * @return
     */
    public static Long getTimeMilliSeconds(String gaeaTime) {
        if (StringUtils.isEmpty(gaeaTime)) {
            return null;
        }
        Long result = null;
        // 不是数字，才是gaea cache用的时间标识
        if (!StringUtils.isNumeric(gaeaTime)) {
            if (StringUtils.endsWith(gaeaTime, MILLISECOND_SUFFIX)) {
                parseTimeNumber(StringUtils.removeEndIgnoreCase(gaeaTime, MILLISECOND_SUFFIX));
            } else if (StringUtils.endsWith(gaeaTime, SECOND_SUFFIX)) {
                result = parseTimeNumber(StringUtils.removeEndIgnoreCase(gaeaTime, SECOND_SUFFIX));
                result = TimeUnit.SECONDS.toMillis(result);
            } else if (StringUtils.endsWith(gaeaTime, MINUTE_SUFFIX)) {
                result = parseTimeNumber(StringUtils.removeEndIgnoreCase(gaeaTime, MINUTE_SUFFIX));
                result = TimeUnit.MINUTES.toMillis(result);
            } else if (StringUtils.endsWith(gaeaTime, HOUR_SUFFIX)) {
                result = parseTimeNumber(StringUtils.removeEndIgnoreCase(gaeaTime, HOUR_SUFFIX));
                result = TimeUnit.HOURS.toMillis(result);
            } else if (StringUtils.endsWith(gaeaTime, DAY_SUFFIX)) {
                result = parseTimeNumber(StringUtils.removeEndIgnoreCase(gaeaTime, DAY_SUFFIX));
                result = TimeUnit.DAYS.toMillis(result);
            } else {
                logger.debug("输入的时间格式可能有问题。转换失败！时间数值={}", gaeaTime);
            }
        }
        return result;
    }

    private static Long parseTimeNumber(String timeNumber) {
        Long result = null;
        if (StringUtils.isNotEmpty(timeNumber)) {
            if (StringUtils.isNumeric(timeNumber)) {
                result = Long.parseLong(timeNumber);
            } else {
                logger.debug("输入的时间格式可能有问题。转换失败！剔除字符时间单位后的数值={}", timeNumber);
            }
        }
        return result;
    }
}
