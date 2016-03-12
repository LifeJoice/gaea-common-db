package org.gaea.data.dataset.service;

import org.gaea.exception.ValidationFailedException;

import java.util.List;
import java.util.Map;

/**
 * Dataset的统一对外服务接口。
 * Created by iverson on 2016/2/23.
 */
public interface GaeaDataSetService {
    List<Map<String, String>> getCommonResults(String id) throws ValidationFailedException;
}
