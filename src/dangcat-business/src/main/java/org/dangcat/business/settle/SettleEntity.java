package org.dangcat.business.settle;

import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.entity.EntityBase;
import org.dangcat.persistence.model.DataStatus;

/**
 * Ω·À„ª˘¿‡°£
 *
 * @author dangcat
 */
public abstract class SettleEntity extends EntityBase implements DataStatus {
    public static final String MaxId = "MaxId";
    private static final long serialVersionUID = 1L;

    @Column(index = 5, isCalculate = true, visible = false)
    private Integer maxId = null;

    public Integer getMaxId() {
        return maxId;
    }

    public void setMaxId(Integer maxId) {
        this.maxId = maxId;
    }
}