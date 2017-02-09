package org.gaea.data.dataset.service;

import org.gaea.data.dataset.domain.GaeaDataSet;
import org.gaea.data.dataset.domain.GaeaDsResultConfig;
import org.gaea.exception.ProcessFailedException;
import org.gaea.exception.SysInitException;
import org.gaea.exception.ValidationFailedException;

import java.util.List;
import java.util.Map;

/**
 * Dataset的统一对外服务接口。
 * Created by iverson on 2016/2/23.
 */
public interface GaeaDataSetService {
    List<Map<String, Object>> getCommonResults(String id, String aliasObjName) throws ValidationFailedException;

    List<Map<String, Object>> getCommonResults(GaeaDsResultConfig resultConfig) throws ValidationFailedException;

    Map<String, GaeaDataSet> getAllDataSets() throws SysInitException;

    void resetDataSets(Map<String, GaeaDataSet> dataSets) throws ProcessFailedException;

    void cacheDataSet(GaeaDataSet dataSet) throws ProcessFailedException;
}
