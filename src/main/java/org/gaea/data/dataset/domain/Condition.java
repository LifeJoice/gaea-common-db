package org.gaea.data.dataset.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by iverson on 2016-7-12 10:52:25.
 */
public class Condition implements Serializable {
    private String propName;
    private String propValue;
    private List propValues; // 如果是in的查询，就是多值。
    private String op;// 单字段条件的比较操作符：eq ne lt gt le ge.这个在处理时会被转义.
    /* fieldOp定义的类型: eq ne lt gt le ge */
    public static final String FIELD_OP_EQ = "eq";
    public static final String FIELD_OP_NE = "ne";
    public static final String FIELD_OP_LT = "lt";
    public static final String FIELD_OP_GT = "gt";
    public static final String FIELD_OP_LE = "le";
    public static final String FIELD_OP_GE = "ge";
    private String condOp;// 不同条件间的操作符，例如：and,or,in等
    private String placeholder; // SQL里面的占位符。如果有的话，当前condition产生的查询条件会替换占位符的内容。
    /**
     * 数据类型。这个和XML SCHEMA的data-type一样。
     * 辅助字段。当查询都是字符串的时候可以无视。但如果是日期之类的，需要有dataType协助转换。
     */
    private String dataType;

    public String getPropName() {
        return propName;
    }

    public void setPropName(String propName) {
        this.propName = propName;
    }

    public String getPropValue() {
        return propValue;
    }

    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }

    public List getPropValues() {
        return propValues;
    }

    public void setPropValues(List propValues) {
        this.propValues = propValues;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getCondOp() {
        return condOp;
    }

    public void setCondOp(String condOp) {
        this.condOp = condOp;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
