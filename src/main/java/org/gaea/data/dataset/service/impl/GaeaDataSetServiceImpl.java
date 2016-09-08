package org.gaea.data.dataset.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.gaea.cache.GaeaCacheProcessor;
import org.gaea.data.dataset.GaeaDataSetDefinition;
import org.gaea.data.dataset.GaeaDataSetResolver;
import org.gaea.data.dataset.domain.GaeaDataSet;
import org.gaea.data.dataset.domain.GaeaDsResultConfig;
import org.gaea.data.dataset.service.GaeaDataSetService;
import org.gaea.exception.ValidationFailedException;
import org.gaea.util.GaeaPropertiesReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public List<Map<String, Object>> getCommonResults(String dsId, String aliasObjName) throws ValidationFailedException {
        if (StringUtils.isEmpty(dsId)) {
            return null;
        }
        return getCommonResults(new GaeaDsResultConfig(dsId, aliasObjName));
    }

    /**
     * 1. 返回数据集的key，统一小写。
     * 2. text , value作为数据集的关键字，提供可配置和转换。（即如果查询SQL结果没有text、value字段，可以配置把某字段在返回页面前改名为text或value）
     *
     * @param resultConfig
     * @return
     * @throws ValidationFailedException
     */
    public List<Map<String, Object>> getCommonResults(GaeaDsResultConfig resultConfig) throws ValidationFailedException {
//        Map<String, String> results = new HashMap<String, String>();
        List<Map<String, Object>> results = null;
        if (gaeaDataSetResolver == null) {
            logger.warn(" Gaea的数据集处理器 GaeaDataSetResolver 未初始化！可能影响系统的整体功能！");
        }
        if (jdbcTemplate == null) {
            logger.warn("缺少Spring的NamedParameterJdbcTemplate bean，无法进行Gaea的数据集框架服务！");
        }
        Map<String, String> params = new HashMap<String, String>();
//        try {
//            gaeaDataSetResolver.getSystemDataSets();
        // 获取数据集定义。可能从数据库读，也可能从缓存获取。
        GaeaDataSet dataSetDef = gaeaDataSetResolver.getDataSet(resultConfig.getDsId());
        GaeaDataSet dsData = null;
        /* 根据数据集定义的SQL（或者定义静态数据）获取数据 */
        if (GaeaDataSet.CACHE_TYPE_STATIC.equals(dataSetDef.getCacheType())) { // 如果定义的是静态数据集，优先从缓存取，并且不刷新。
            dsData = gaeaCacheProcessor.getHashValue(cacheProperties.get(GaeaDataSetDefinition.GAEA_DATASET_SCHEMA), resultConfig.getDsId());
            results = dsData.getStaticResults();
        } else if (GaeaDataSet.CACHE_TYPE_NONE.equals(dataSetDef.getCacheType())) { // 如果定义是不缓存，每次都查询数据。
            results = jdbcTemplate.queryForList(dataSetDef.getSql(), params);
        } else if (GaeaDataSet.CACHE_TYPE_AUTO.equals(dataSetDef.getCacheType())) { // 自动缓存
            dsData = gaeaCacheProcessor.getHashValue(cacheProperties.get(GaeaDataSetDefinition.GAEA_DATASET_SCHEMA), resultConfig.getDsId());
            if (dsData == null || dsData.getDsResults() == null) {
                results = jdbcTemplate.queryForList(dataSetDef.getSql(), params);
                // 根据具体缓存策略，进行缓存处理。
                gaeaCacheProcessor.cachedByStrategy(dataSetDef, results);
            }
        }
//        results = jdbcTemplate.queryForList(dataSetDef.getSql(), params);
        results = reconstruct(results, resultConfig);
//        GaeaDataSet ds = gaeaCacheProcessor.getHashValue(cacheProperties.get(GaeaDataSetDefinition.GAEA_DATASET_SCHEMA), "DS_IS_ENABLED");
//        for (Object key : dsData.getStaticResults().keySet()) {
//            System.out.println(" key: " + key + " value: " + dsData.getStaticResults().get(key));
//        }
        for (Map map : results) {
            System.out.println(" text: " + map.get("text") + " value: " + map.get("value"));
        }
//        } catch (ValidationFailedException e) {
//            throw new ValidationFailedException("获取Gaea数据集系统的数据集失败！-->" + e.getMessage(), e);
//        }
        return results;
    }

    /**
     * 对结果集进行数据清洗。按照前端的需要，把数据的命名、大小写等进行后期处理。
     * 1. 返回数据集的key，统一小写。
     * 2. text , value作为数据集的关键字，提供可配置和转换。（即如果查询SQL结果没有text、value字段，可以配置把某字段在返回页面前改名为text或value）
     *
     * @param results
     * @param config
     * @return
     */
    private List<Map<String, Object>> reconstruct(List<Map<String, Object>> results, GaeaDsResultConfig config) {
        List<Map<String, Object>> newResults = new ArrayList<Map<String, Object>>();
        if (results != null && !results.isEmpty()) {
            for (Map<String, Object> dsRow : results) {
                Map<String, Object> newDsRow = new HashMap<String, Object>();
                boolean hasRenameTextName = false;
                boolean hasRenameValueName = false;
                for (String itemName : dsRow.keySet()) {
                    String itemValue = dsRow.get(itemName) == null ? "" : dsRow.get(itemName).toString();
                    String newItemName = "";
                    // 统一处理，把结果集的key都改为小写。
                    newItemName = itemName.toLowerCase();
                    // 如果配置了text对应的字段，把对应JSON的key改为'text'
                    if (StringUtils.isNotEmpty(config.getTextName()) && config.getTextName().equalsIgnoreCase(itemName)) {
                        newItemName = GaeaDsResultConfig.DEFAULT_DS_TEXT_NAME;
                    }
                    // 如果配置了value对应的字段，把对应JSON的key改为'value'
                    if (StringUtils.isNotEmpty(config.getValueName()) && config.getValueName().equalsIgnoreCase(itemName)) {
                        newItemName = GaeaDsResultConfig.DEFAULT_DS_VALUE_NAME;
                    }
                    // 给结果集中的所有key加上别名
                    if (StringUtils.isNotEmpty(config.getAliasObjName())) {
                        // 命名规则：别名 + . + 原来的key。例如：name -> user.name
                        newItemName = config.getAliasObjName() + "." + newItemName;
                    }
                    newDsRow.put(newItemName, itemValue);
                }
                newResults.add(newDsRow);
            }
        }
        return newResults;
    }

    public GaeaPropertiesReader getCacheProperties() {
        return cacheProperties;
    }

    public void setCacheProperties(GaeaPropertiesReader cacheProperties) {
        this.cacheProperties = cacheProperties;
    }
}
