package org.gaea.data.dataset.domain;

import java.io.Serializable;

/**
 * 负责从API中读取数据response的定义。
 * Created by iverson on 2017年11月30日16:18:54
 */
public class XmlApiResponse implements Serializable {
    private XmlApiResponseDataExtract dataExtract;

    public XmlApiResponseDataExtract getDataExtract() {
        return dataExtract;
    }

    public void setDataExtract(XmlApiResponseDataExtract dataExtract) {
        this.dataExtract = dataExtract;
    }
}
