package org.gaea.data.dataset.domain;

import java.io.Serializable;

/**
 * Created by iverson on 2017年11月30日16:19:28
 */
public class XmlDsParam implements Serializable {
    /* 变量名param name */
    private String name;
    /* 变量值param value */
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
