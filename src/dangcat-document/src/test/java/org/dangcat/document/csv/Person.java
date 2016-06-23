package org.dangcat.document.csv;

import org.dangcat.persistence.annotation.Column;

@org.dangcat.persistence.annotation.Table
public class Person {
    @Column(isPrimaryKey = true, index = 0)
    private Integer id = null;
    @Column(displaySize = 10, index = 2)
    private String name = null;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
