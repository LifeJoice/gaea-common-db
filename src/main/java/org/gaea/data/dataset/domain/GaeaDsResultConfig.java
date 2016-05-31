package org.gaea.data.dataset.domain;

/**
 * Created by iverson on 2016/5/26.
 */
public class GaeaDsResultConfig {
    private String dsId;// 数据集id
    private String aliasObjName;// 数据集的结果的对象别名.会在每个key前面加上"对象名."
    private String textName;// text对应的字段名(或sql语句里某字段)
    public static final String DEFAULT_DS_TEXT_NAME = "text";// 数据集的结果项，默认的显示字段名(类似下拉框的显示文本字段)
    private String valueName;// 值对应的字段名(或sql语句里某字段)
    public static final String DEFAULT_DS_VALUE_NAME = "value";// 数据集的结果项，默认的值(对应textName的值)

    public GaeaDsResultConfig() {
    }

    public GaeaDsResultConfig(String dsId, String aliasObjName) {
        this.dsId = dsId;
        this.aliasObjName = aliasObjName;
    }

    public String getDsId() {
        return dsId;
    }

    public void setDsId(String dsId) {
        this.dsId = dsId;
    }

    public String getAliasObjName() {
        return aliasObjName;
    }

    public void setAliasObjName(String aliasObjName) {
        this.aliasObjName = aliasObjName;
    }

    public String getTextName() {
        return textName;
    }

    public void setTextName(String textName) {
        this.textName = textName;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }
}
