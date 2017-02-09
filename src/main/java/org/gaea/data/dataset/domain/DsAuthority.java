package org.gaea.data.dataset.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by iverson on 2016/12/20.
 */
public interface DsAuthority extends Serializable {
    public List<? extends DsAuthRole> getRoles();
}
