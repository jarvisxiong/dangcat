package org.dangcat.persistence.domain;

import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Table;

@Table
public class BillDetail {
    public static final String Amount = "Amount";
    public static final String BillId = "BillId";
    public static final String Id = "Id";
    public static final String Name = "Name";

    @Column
    private double amount;

    @Column
    private Integer billId;

    @Column(isPrimaryKey = true, isAutoIncrement = true)
    private Integer id;

    @Column
    private String name;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Integer getBillId() {
        return billId;
    }

    public void setBillId(Integer billId) {
        this.billId = billId;
    }

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
