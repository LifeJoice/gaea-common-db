package org.gaea.data.dataset.domain;

import org.gaea.data.dataset.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据集的单个数据项，可以是一行，或者一个< data-element >.
 * Created by iverson on 2016/10/10.
 */
public class DataItem implements BaseResultSet, Item {
    private String value;
    private String text;
    private List<Map<String, Object>> otherValues = new ArrayList<Map<String, Object>>();

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Map<String, Object>> getOtherValues() {
        return otherValues;
    }

    public void setOtherValues(List<Map<String, Object>> otherValues) {
        this.otherValues = otherValues;
    }
}
