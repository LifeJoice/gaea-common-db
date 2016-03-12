package org.gaea.data.domain;

import java.io.Serializable;

/**
 * Created by iverson on 2016/2/28.
 */
public class GaeaDataSource implements Serializable{
    private String name;
    private String code;
    private String beanRef;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBeanRef() {
        return beanRef;
    }

    public void setBeanRef(String beanRef) {
        this.beanRef = beanRef;
    }
}
