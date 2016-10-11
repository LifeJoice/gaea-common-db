package org.gaea.data.system;

import org.apache.commons.lang3.StringUtils;
import org.gaea.data.dataset.GaeaDataSetResolver;
import org.gaea.data.dataset.domain.GaeaDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 系统的所有DataSet的工厂类。
 * Created by iverson on 2016/10/10.
 */
@Component
public class SystemDataSetFactory implements ApplicationContextAware {

    private static Logger logger = LoggerFactory.getLogger(SystemDataSetFactory.class);
    private static ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        applicationContext = ac;
    }

    /**
     * 必须在Spring启动后方可以使用。
     *
     * @param dsId
     * @return
     */
    public static GaeaDataSet getDataSet(String dsId) {
        GaeaDataSet gaeaDataSet = null;
        if (StringUtils.isEmpty(dsId)) {
            throw new IllegalArgumentException("dataSet id不允许为空！");
        }
        if (applicationContext != null) {
            GaeaDataSetResolver dataSetResolver = applicationContext.getBean(GaeaDataSetResolver.class);
            if (dataSetResolver != null) {
                gaeaDataSet = dataSetResolver.getDataSet(dsId);
            }
        } else {
            logger.debug("无法获取GaeaDataSet，因为applicationContext为空，获取不到GaeaDataSetResolver.");
        }
        return gaeaDataSet;
    }
}
