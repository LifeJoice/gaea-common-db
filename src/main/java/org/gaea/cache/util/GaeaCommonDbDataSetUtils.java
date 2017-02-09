package org.gaea.cache.util;

import org.apache.commons.collections.CollectionUtils;
import org.gaea.data.dataset.domain.DataItem;
import org.gaea.data.dataset.domain.GaeaDsResultConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 协助数据集转换的一些工具集合。
 * Created by iverson on 2017/2/9.
 */
public class GaeaCommonDbDataSetUtils {
    private static final Logger logger = LoggerFactory.getLogger(GaeaCommonDbDataSetUtils.class);

    public static List<Map<String, Object>> convertStaticDs(List<DataItem> dataItemList) {
        if (CollectionUtils.isEmpty(dataItemList)) {
            return null;
        }
        List<Map<String, Object>> newDataList = new ArrayList<Map<String, Object>>();

        for (DataItem item : dataItemList) {
            Map<String, Object> newDataMap = new HashMap<String, Object>();
            // 写入值。因为静态的数据集，值不多，就两个：text和value
            newDataMap.put(GaeaDsResultConfig.DEFAULT_DS_TEXT_NAME, item.getText());
            newDataMap.put(GaeaDsResultConfig.DEFAULT_DS_VALUE_NAME, item.getValue());

            newDataList.add(newDataMap);
        }
        return newDataList;
    }
}
