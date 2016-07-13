package org.gaea.data.dataset.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by iverson on 2016-7-12 10:52:05.
 */
public class ConditionSet  implements Serializable {
    private String id;
    private List<Condition> conditions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }
}
