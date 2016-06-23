package org.dangcat.persistence.domain;

import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Relation;
import org.dangcat.persistence.annotation.Table;

import java.util.LinkedHashSet;
import java.util.Set;

@Table
public class BillInfo
{
    public static final String Amount = "Amount";
    public static final String Id = "Id";
    public static final String Name = "Name";

    @Column
    private double amount = 0;

    @Relation(parentFieldNames = { "id" }, childFieldNames = { "billId" })
    private Set<BillDetail> billDetails = new LinkedHashSet<BillDetail>();

    @Column(isPrimaryKey = true, isAutoIncrement = true)
    private Integer id;

    @Column
    private String name;

    public void addBillDetail(BillDetail billDetail)
    {
        if (this.billDetails.add(billDetail))
            this.amount += billDetail.getAmount();
    }

    public void calculateTotal()
    {
        double totalAmount = 0;
        for (BillDetail billDetail : this.getBillDetails())
            totalAmount += billDetail.getAmount();
        this.amount = totalAmount;
    }

    public double getAmount()
    {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Set<BillDetail> getBillDetails()
    {
        return billDetails;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void removeBillDetail(BillDetail billDetail)
    {
        if (this.billDetails.remove(billDetail))
            this.amount -= billDetail.getAmount();
    }
}
