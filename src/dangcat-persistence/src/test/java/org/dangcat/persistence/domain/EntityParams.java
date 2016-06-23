package org.dangcat.persistence.domain;

import java.util.Date;

import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Param;
import org.dangcat.persistence.annotation.Params;
import org.dangcat.persistence.annotation.Table;

@Table
public class EntityParams
{
    @Column(isPrimaryKey = true, isAutoIncrement = true)
    @Param(name = "idParams1", value = "1000", classType = Integer.class)
    private Integer id;

    @Column(displaySize = 40)
    @Params( { @Param(name = "nameParams1", value = "nameParams1-value"), @Param(name = "nameParams2", value = "2012-12-25", classType = Date.class),
            @Param(name = "nameParams3", value = "true", classType = Boolean.class) })
    private String name;

    public Integer getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

}
