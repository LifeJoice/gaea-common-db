package org.gaea.data.dataset.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Column支持嵌套。只要把type设置为group即可以分组。（未实现）
 * Created by Iverson on 2015/6/29.
 */
public class GaeaColumn implements Serializable {
    private String id;
    private String name;
    private String dbColumnName;
    //    private Boolean primaryKey = false;// 是否主键
    private String dataType;// 数据的类型。暂时主要是SQL查询时，要转换类型去查，例如日期类的，直接用字符串是查不出来的。 by Iverson 2016-6-21
    // yyyy-mm-dd
    private String datetimeFormat;
    private String dataSetId; // 数据集id。一般没有。有的话，会把该列的值按数据集对应的text:value作转换。

    // 构造方法

    public GaeaColumn() {
    }

    public GaeaColumn(String id, String name, String label, String dbColumnName, Boolean visible, String htmlWidth, String dataType, String dataSetId, String datetimeFormat) {
        this.id = id;
        this.name = name;
        this.dbColumnName = dbColumnName;
        this.dataType = dataType;
        this.datetimeFormat = datetimeFormat;
        this.dataSetId = dataSetId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDbColumnName() {
        return dbColumnName;
    }

    public void setDbColumnName(String dbColumnName) {
        this.dbColumnName = dbColumnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDatetimeFormat() {
        return datetimeFormat;
    }

    public void setDatetimeFormat(String datetimeFormat) {
        this.datetimeFormat = datetimeFormat;
    }

    public String getDataSetId() {
        return dataSetId;
    }

    public void setDataSetId(String dataSetId) {
        this.dataSetId = dataSetId;
    }
}
