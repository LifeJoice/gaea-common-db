package org.gaea.data.dataset;

import org.gaea.data.dataset.domain.DataItem;
import org.gaea.exception.ValidationFailedException;

import java.util.List;
import java.util.Map;

/**
 * 自定义数据处理器的接口定义. 任何要实现自己的数据集的数据编码，就必须实现这个接口。并把它变成个spring bean。然后配置到数据集中。
 * Created by iverson on 2018-4-23 17:03:09
 */
public interface DsProcessor {
    /* 执行数据的转换或者生成 */
    public List<DataItem> dataProcess(List<Map<String, Object>> origData, Map contextParams) throws ValidationFailedException;

    /* 相关实现需要的额外参数，可以从xml传递给类 */
    public Map<String, Object> getParams();

    public void setParams(Map<String, Object> params);
}
