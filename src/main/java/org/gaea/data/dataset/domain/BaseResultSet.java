package org.gaea.data.dataset.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 数据集的数据的domain基础。必须提供text,value, otherValues这几个属性。
 * Created by iverson on 2016/10/10.
 */
public interface BaseResultSet extends Serializable {
    public String getText();// 数据集某项的文字。例如：对应前端下拉框的option显示

    public String getValue();// 数据集某项的文字。例如：对应前端下拉框的value

    public List<Map<String, Object>> getOtherValues(); // 除了text,value外，如果还有其他项，可以放在这个列表中。其中每个map，key=value,value=text
}
