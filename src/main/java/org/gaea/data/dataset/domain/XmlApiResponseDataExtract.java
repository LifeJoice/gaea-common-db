package org.gaea.data.dataset.domain;

import java.io.Serializable;

/**
 * 负责从response中抽取所需数据的定义。
 * Created by iverson on 2017年11月30日16:22:34
 */
public class XmlApiResponseDataExtract implements Serializable {
    /* response的数据体部分的定义、配置 */
    private XmlApiResponseData apiResponseData;
    /* 数据分页相关的定义（只response返回了部分属性） */
    private XmlApiPage apiPage;

    public XmlApiResponseData getApiResponseData() {
        return apiResponseData;
    }

    public void setApiResponseData(XmlApiResponseData apiResponseData) {
        this.apiResponseData = apiResponseData;
    }

    public XmlApiPage getApiPage() {
        return apiPage;
    }

    public void setApiPage(XmlApiPage apiPage) {
        this.apiPage = apiPage;
    }
}
