package org.gaea.data.dataset.service.impl;

import org.gaea.cache.GaeaCacheProcessor;
import org.gaea.data.dataset.GaeaDataSetResolver;
import org.gaea.data.dataset.domain.GaeaDataSet;
import org.gaea.data.dataset.service.GaeaDataSetService;
import org.gaea.exception.ValidationFailedException;
import org.gaea.util.GaeaPropertiesReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by iverson on 2016/2/23.
 */
@Service
public class GaeaDataSetServiceImpl implements GaeaDataSetService {

    private final Logger logger = LoggerFactory.getLogger(GaeaDataSetServiceImpl.class);
    @Autowired(required = false)
    private GaeaDataSetResolver gaeaDataSetResolver;
    @Autowired(required = false)
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired(required = false)
    @Qualifier("cachePropReader")
    private GaeaPropertiesReader cacheProperties;
    @Autowired(required = false)
    private GaeaCacheProcessor gaeaCacheProcessor;

    public List<Map<String, String>> getCommonResults(String id) throws ValidationFailedException {
//        Map<String, String> results = new HashMap<String, String>();
        List<Map<String,String>> results = null;
        if (gaeaDataSetResolver == null) {
            logger.warn(" Gaea的数据集处理器 GaeaDataSetResolver 未初始化！可能影响系统的整体功能！");
        }
        if (jdbcTemplate == null) {
            logger.warn("缺少Spring的NamedParameterJdbcTemplate bean，无法进行Gaea的数据集框架服务！");
        }
//        try {
//            gaeaDataSetResolver.getSystemDataSets();
            GaeaDataSet dataSet = gaeaDataSetResolver.getDataSet(id);
            results = jdbcTemplate.query(dataSet.getSql(),new RowMapper<Map<String,String>>() {
                public Map<String,String> mapRow(ResultSet resultSet, int i) throws SQLException {
                    Map<String,String> result = new HashMap<String, String>();
                    result.put(resultSet.getString("VALUE"),resultSet.getString("TEXT"));
                    return result;
                }
            });
        GaeaDataSet ds = gaeaCacheProcessor.getHashValue(cacheProperties.get("gaea.dataset")+cacheProperties.get("gaea.dataset.schema"),"DS_IS_ENABLED");
        for(Object key:ds.getSimpleResults().keySet()){
            System.out.println(" key: "+key+" value: "+ds.getSimpleResults().get(key));
        }
//        } catch (ValidationFailedException e) {
//            throw new ValidationFailedException("获取Gaea数据集系统的数据集失败！-->" + e.getMessage(), e);
//        }
        return results;
    }

    public GaeaPropertiesReader getCacheProperties() {
        return cacheProperties;
    }

    public void setCacheProperties(GaeaPropertiesReader cacheProperties) {
        this.cacheProperties = cacheProperties;
    }
}
