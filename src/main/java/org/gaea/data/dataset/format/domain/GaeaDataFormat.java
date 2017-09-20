package org.gaea.data.dataset.format.domain;

import java.io.Serializable;

/**
 * Created by iverson on 2017/9/5.
 */
public class GaeaDataFormat implements Serializable {
    private GaeaFormatNode node;

    public GaeaFormatNode getNode() {
        return node;
    }

    public void setNode(GaeaFormatNode node) {
        this.node = node;
    }
}
