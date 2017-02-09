package org.gaea.data.system;

import org.apache.commons.lang3.StringUtils;
import org.gaea.data.dataset.GaeaDataSetResolver;
import org.gaea.data.dataset.domain.GaeaDataSet;
import org.gaea.data.dataset.service.GaeaDataSetService;
import org.gaea.exception.ProcessFailedException;
import org.gaea.exception.SysInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 系统的所有DataSet的工厂类。
 * Created by iverson on 2016/10/10.
 */
@Component
public class SystemDataSetFactory implements ApplicationContextAware {

    private static Logger logger = LoggerFactory.getLogger(SystemDataSetFactory.class);
    private static ApplicationContext applicationContext;
    private static GaeaDataSetService gaeaDataSetService;

    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        applicationContext = ac;
        gaeaDataSetService = applicationContext.getBean(GaeaDataSetService.class);
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

    /**
     * 获取系统缓存的所有数据集定义。
     *
     * @return
     * @throws SysInitException
     */
    public static Map<String, GaeaDataSet> getAllDataSets() throws SysInitException {
        if (gaeaDataSetService == null) {
            throw new SysInitException("系统未初始化GaeaDataSetService。GaeaDataSetService为空。");
        }
        return gaeaDataSetService.getAllDataSets();
    }

    public static void cacheDataSet(GaeaDataSet dataSet) throws ProcessFailedException, SysInitException {
        if (gaeaDataSetService == null) {
            throw new SysInitException("系统未初始化GaeaDataSetService。GaeaDataSetService为空。");
        }
        gaeaDataSetService.cacheDataSet(dataSet);
    }
}
