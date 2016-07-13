package org.gaea.data.dataset.domain;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by iverson on 2016-7-12 10:51:38.
 */
public class Where implements Serializable{
    private Map<String,ConditionSet> conditionSets; // key=id , value=ConditionSet

    public Map<String, ConditionSet> getConditionSets() {
        return conditionSets;
    }

    public void setConditionSets(Map<String, ConditionSet> conditionSets) {
        this.conditionSets = conditionSets;
    }
}
