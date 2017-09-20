package org.gaea.data.convertor;

/**
 * 表示一个数据转换的定义。
 * <p><b>主要用于</b></p>
 * <ul>
 * <li>DataSet读出来把数据库字段转换为合适的json key</li>
 * <li>（以后）view的grid的column，也是从数据集转换为合适的key，还有dataType转换</li>
 * <li>{@code <data-format>}的数据库字段名到具体key的转换。</li>
 * </ul>
 * <p/>
 * Created by iverson on 2017/9/5.
 */
public class DataConvertor {
    /**
     * 来源的名称
     */
    private String fromName;
    /**
     * 要把fromName改为什么名称
     */
    private String toName;
    /**
     * 数据类型。夹杂着数据转换。
     */
    private String dataType;

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
