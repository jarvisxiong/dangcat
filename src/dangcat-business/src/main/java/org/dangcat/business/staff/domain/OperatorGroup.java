package org.dangcat.business.staff.domain;

import org.dangcat.business.staff.service.impl.OperatorGroupCalculator;
import org.dangcat.persistence.annotation.Calculator;
import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Table;

@Table
@Calculator(OperatorGroupCalculator.class)
public class OperatorGroup extends OperatorGroupBase
{
    public static final String Description = "Description";
    private static final long serialVersionUID = 1L;

    @Column(index = 2, displaySize = 200)
    private String description = null;
    @Column(index = 4, displaySize = 20, isCalculate = true, isReadonly = true)
    private String parentName = null;

    public OperatorGroup()
    {
    }

    public String getDescription()
    {
        return this.description;
    }

    public String getParentName()
    {
        return parentName;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setParentName(String parentName)
    {
        this.parentName = parentName;
    }
}