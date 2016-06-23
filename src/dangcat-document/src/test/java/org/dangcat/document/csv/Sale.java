package org.dangcat.document.csv;

import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Table;

@Table
public class Sale extends Person
{
    @Column(index = 1)
    private Integer sex = null;

    public Integer getSex()
    {
        return this.sex;
    }

    public void setSex(Integer sex)
    {
        this.sex = sex;
    }

}
