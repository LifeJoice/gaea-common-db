package org.gaea.data.dataset.domain;

import java.io.Serializable;

/**
 * Created by iverson on 2017年11月30日16:18:13
 */
public class XmlApiRequest implements Serializable {
    private String url;
    private XmlApiPage apiPage;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public XmlApiPage getApiPage() {
        return apiPage;
    }

    public void setApiPage(XmlApiPage apiPage) {
        this.apiPage = apiPage;
    }
}
