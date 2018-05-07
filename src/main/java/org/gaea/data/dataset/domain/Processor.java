package org.gaea.data.dataset.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * （数据集）数据处理器定义。
 * <p>
 * 提供编码的方式，生成数据集的数据。
 * </p>
 * <p>可以对数据做二次处理，也可以直接写代码生成数据。</p>
 * Created by iverson on 2018-4-23 16:51:41
 */
public class Processor implements Serializable {
    /* spring bean的引用. 负责处理数据逻辑处理的bean。 */
    private String beanRef;
    /* 就是元素的名称 */
    private String type;
    public static final String TYPE_PROCESSOR = "processor";
    /* 给对应的beanRef的相关参数 */
    private Map<String, Object> params;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBeanRef() {
        return beanRef;
    }

    public void setBeanRef(String beanRef) {
        this.beanRef = beanRef;
    }

    public Map<String, Object> getParams() {
        if (params == null) {
            params = new HashMap<String, Object>();
        }
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
