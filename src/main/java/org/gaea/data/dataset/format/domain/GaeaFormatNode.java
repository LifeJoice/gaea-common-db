package org.gaea.data.dataset.format.domain;

import org.gaea.data.convertor.DataConvertor;
import org.gaea.data.dataset.domain.ConditionSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by iverson on 2017/9/5.
 */
public class GaeaFormatNode implements Serializable {
    /**
     * 当前节点的子节点
     */
    private List<GaeaFormatNode> nodes;
    /**
     * 就是key。如果未指定，这个默认也就是dataConvertor的toName。
     */
    private String name;
    /**
     * 节点的数据的转换定义
     */
    private DataConvertor dataConvertor;
    /**
     * 节点类型
     * <ul><li>默认：对象</li><li>list: 数组</li></ul>
     */
    private String type = "list";
    public static final String TYPE_DEFAULT = "object";
    public static final String TYPE_LIST = "list";
    /**
     * 是否可以作为当前层级的主键用。
     * 这个主要是：
     * 在把行数据转换为json树结构的时候，对于树的每一层需要一个唯一标识符。然后会把所有数据先按标识符分层级先规整，再转换成树结构。
     */
    private boolean primary = false;

    public List<GaeaFormatNode> getNodes() {
        if (nodes == null) {
            nodes = new ArrayList<GaeaFormatNode>();
        }
        return nodes;
    }

    public void setNodes(List<GaeaFormatNode> nodes) {
        this.nodes = nodes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataConvertor getDataConvertor() {
        return dataConvertor;
    }

    public void setDataConvertor(DataConvertor dataConvertor) {
        this.dataConvertor = dataConvertor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }
}
