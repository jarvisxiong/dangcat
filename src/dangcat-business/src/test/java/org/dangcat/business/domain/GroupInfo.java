package org.dangcat.business.domain;

import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Table;
import org.dangcat.persistence.entity.EntityBase;

@Table
public class GroupInfo extends EntityBase {
    public static final String Id = "Id";
    public static final String Name = "Name";
    private static final long serialVersionUID = 1L;

    @Column(isPrimaryKey = true, isAutoIncrement = true)
    private Integer id;

    @Column(displaySize = 20)
    private String name = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
