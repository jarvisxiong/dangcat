package org.dangcat.persistence.domain;

import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Table;
import org.dangcat.persistence.model.GenerationType;

@Table
public class PersonInfo
{
    public static final String Id = "Id";
    public static final String Name = "Name";

    @Column(isPrimaryKey = true, isAutoIncrement = true, generationType = GenerationType.IDENTITY)
    private Integer id;

    @Column(displaySize = 20)
    private String name;

    public Integer getId()
    {
        return this.id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
