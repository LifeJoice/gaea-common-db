package org.gaea.data.dataset.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 通过接口调用数据的分页相关的定义。
 * Created by iverson on 2017年11月30日17:04:17
 */
public class XmlApiPage implements Serializable {
    /**
     * 数据列表起始位置。
     * 这里有两种可能：按页的起始位置，或是按行的起始位置。
     * 由于这一般是包含的关系，所以默认从1开始。
     */
    private int startNum = 1;
    /**
     * 数据列表分页的变量名。
     * 这里有两种可能：按页的变量名，或是按起始位置的变量名。
     * 这个取决于配置{@code <paging>}的pagingBy是start还是page
     */
    private String paramName;
    /* 每页多少条的变量名 */
    private String sizeParamName;
    /* 分页接口有两种可能：按页数分(page)，或是按起始位置分(start)。这决定着Gaea框架的分页对接第三方的转换。 */
    private String pagingBy = "page"; // value= page|start
    public static final String PAGING_BY_DEFAULT = "page";
    public static final String PAGING_BY_START = "start";
    /* 接口返回的数据总行数的变量名 */
    private String totalElementsParamName;
    /* <paging>的<params>。扩展，暂时没用。 */
    private List<XmlDsParam> dsParamList;

    public int getStartNum() {
        return startNum;
    }

    public void setStartNum(int startNum) {
        this.startNum = startNum;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getSizeParamName() {
        return sizeParamName;
    }

    public void setSizeParamName(String sizeParamName) {
        this.sizeParamName = sizeParamName;
    }

    public String getPagingBy() {
        return pagingBy;
    }

    public void setPagingBy(String pagingBy) {
        this.pagingBy = pagingBy;
    }

    public String getTotalElementsParamName() {
        return totalElementsParamName;
    }

    public void setTotalElementsParamName(String totalElementsParamName) {
        this.totalElementsParamName = totalElementsParamName;
    }

    public List<XmlDsParam> getDsParamList() {
        if (dsParamList == null) {
            dsParamList = new ArrayList<XmlDsParam>();
        }
        return dsParamList;
    }

    public void setDsParamList(List<XmlDsParam> dsParamList) {
        this.dsParamList = dsParamList;
    }
}
