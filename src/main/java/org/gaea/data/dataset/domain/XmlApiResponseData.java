package org.gaea.data.dataset.domain;

import java.io.Serializable;

/**
 * 通过接口返回数据的数据主体部分相关的配置。
 * Created by iverson on 2017年12月1日11:52:07
 */
public class XmlApiResponseData implements Serializable {
    /* 数据列表的变量名 */
    private String paramName;

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }
}
