package org.gaea.data.dataset.domain;

import java.io.Serializable;

/**
 * Created by iverson on 2016-7-12 10:52:25.
 */
public class Condition implements Serializable {
    private String field;
    private String value;
    private String fieldOp;// 单字段条件的比较操作符：eq ne lt gt le ge.这个在处理时会被转义.
    /* fieldOp定义的类型: eq ne lt gt le ge */
    public static final String FIELD_OP_EQ = "eq";
    public static final String FIELD_OP_NE = "ne";
    public static final String FIELD_OP_LT = "lt";
    public static final String FIELD_OP_GT = "gt";
    public static final String FIELD_OP_LE = "le";
    public static final String FIELD_OP_GE = "ge";
    private String condOp;// 不同条件间的操作符，例如：and,or,in等
    private String placeholder; // SQL里面的占位符。如果有的话，当前condition产生的查询条件会替换占位符的内容。

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFieldOp() {
        return fieldOp;
    }

    public void setFieldOp(String fieldOp) {
        this.fieldOp = fieldOp;
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
}
