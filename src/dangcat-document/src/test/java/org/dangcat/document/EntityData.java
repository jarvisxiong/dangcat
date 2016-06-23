package org.dangcat.document;

import org.dangcat.persistence.annotation.*;
import org.dangcat.persistence.entity.LoadEntityContext;
import org.dangcat.persistence.entity.SaveEntityContext;

import java.util.Arrays;
import java.util.Date;

@Table("ModelData")
public class EntityData
{
    public static final String FieldA = "FieldA";
    public static final String FieldB = "FieldB";
    public static final String FieldC = "FieldC";
    public static final String FieldD = "FieldD";
    public static final String FieldE = "FieldE";
    public static final String FieldF = "FieldF";
    public static final String FieldG = "FieldG";
    public static final String FieldH = "FieldH";
    public static final String Id = "Id";

    private int afterCommitCount = 0;
    private int afterDeleteCount = 0;

    private int afterInsertCount = 0;
    private int afterLoadCount = 0;
    private int afterSaveCount = 0;
    private int beforeDeleteCount = 0;
    private int beforeInsertCount = 0;
    private int beforeSaveCount = 0;
    @Column(displaySize = 40)
    private String fieldA;
    @Column
    private Integer fieldB;
    @Column
    private Double fieldC;
    @Column
    private Long fieldD;
    @Column
    private Date fieldE;
    @Column(displaySize = 1)
    private char[] fieldF;
    @Column
    private Short fieldG;
    @Column
    private byte[] fieldH;
    @Column(isPrimaryKey = true, isAutoIncrement = true)
    private Integer id;

    @AfterCommit
    public void afterCommit()
    {
        afterCommitCount++;
    }

    @AfterCommit
    public void afterCommit(SaveEntityContext saveEntityContext)
    {
        afterCommitCount++;
    }

    @AfterDelete
    public void afterDelete()
    {
        afterDeleteCount++;
    }

    @AfterDelete
    public void afterDelete(SaveEntityContext saveEntityContext)
    {
        afterDeleteCount++;
    }

    @AfterInsert
    public void AfterInsert()
    {
        afterInsertCount++;
    }

    @AfterInsert
    public void AfterInsert(SaveEntityContext saveEntityContext)
    {
        afterInsertCount++;
    }

    @AfterLoad
    public void afterLoad()
    {
        afterLoadCount++;
    }

    @AfterLoad
    public void afterLoad(LoadEntityContext loadEntityContext)
    {
        afterLoadCount++;
    }

    @AfterSave
    public void afterSave()
    {
        afterSaveCount++;
    }

    @AfterSave
    public void afterSave(SaveEntityContext saveEntityContext)
    {
        afterSaveCount++;
    }

    @BeforeDelete
    public void beforeDelete()
    {
        beforeDeleteCount++;
    }

    @BeforeDelete
    public void beforeDelete(SaveEntityContext saveEntityContext)
    {
        beforeDeleteCount++;
    }

    @BeforeInsert
    public void beforeInsert()
    {
        beforeInsertCount++;
    }

    @BeforeInsert
    public void beforeInsert(SaveEntityContext saveEntityContext)
    {
        beforeInsertCount++;
    }

    @BeforeSave
    public void beforeSave()
    {
        beforeSaveCount++;
    }

    @BeforeSave
    public void beforeSave(SaveEntityContext saveEntityContext)
    {
        beforeSaveCount++;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        EntityData other = (EntityData) obj;
        if (id != null)
            return id.equals(other.id);

        if (fieldA == null)
        {
            if (other.fieldA != null)
                return false;
        }
        else if (!fieldA.equals(other.fieldA))
            return false;
        if (fieldB == null)
        {
            if (other.fieldB != null)
                return false;
        }
        else if (!fieldB.equals(other.fieldB))
            return false;
        if (fieldC == null)
        {
            if (other.fieldC != null)
                return false;
        }
        else if (!fieldC.equals(other.fieldC))
            return false;
        if (fieldD == null)
        {
            if (other.fieldD != null)
                return false;
        }
        else if (!fieldD.equals(other.fieldD))
            return false;
        if (fieldE == null)
        {
            if (other.fieldE != null)
                return false;
        }
        else if (!fieldE.equals(other.fieldE))
            return false;
        if (!Arrays.equals(fieldF, other.fieldF))
            return false;
        if (fieldG == null)
        {
            if (other.fieldG != null)
                return false;
        }
        else if (!fieldG.equals(other.fieldG))
            return false;
        return Arrays.equals(fieldH, other.fieldH);
    }

    public int getAfterCommitCount()
    {
        return afterCommitCount;
    }

    public int getAfterDeleteCount()
    {
        return afterDeleteCount;
    }

    public int getAfterInsertCount()
    {
        return afterInsertCount;
    }

    public int getAfterLoadCount()
    {
        return afterLoadCount;
    }

    public int getAfterSaveCount()
    {
        return afterSaveCount;
    }

    public int getBeforeDeleteCount()
    {
        return beforeDeleteCount;
    }

    public int getBeforeInsertCount()
    {
        return beforeInsertCount;
    }

    public int getBeforeSaveCount()
    {
        return beforeSaveCount;
    }

    public String getFieldA()
    {
        return fieldA;
    }

    public void setFieldA(String fieldA) {
        this.fieldA = fieldA;
    }

    public Integer getFieldB()
    {
        return fieldB;
    }

    public void setFieldB(Integer fieldB) {
        this.fieldB = fieldB;
    }

    public Double getFieldC()
    {
        return fieldC;
    }

    public void setFieldC(Double fieldC) {
        this.fieldC = fieldC;
    }

    public Long getFieldD()
    {
        return fieldD;
    }

    public void setFieldD(Long fieldD) {
        this.fieldD = fieldD;
    }

    public Date getFieldE()
    {
        return fieldE;
    }

    public void setFieldE(Date fieldE) {
        this.fieldE = fieldE;
    }

    public char[] getFieldF()
    {
        return fieldF;
    }

    public void setFieldF(char[] fieldF) {
        this.fieldF = fieldF;
    }

    public Short getFieldG()
    {
        return fieldG;
    }

    public void setFieldG(Short fieldG) {
        this.fieldG = fieldG;
    }

    public byte[] getFieldH()
    {
        return fieldH;
    }

    public void setFieldH(byte[] fieldH) {
        this.fieldH = fieldH;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public int hashCode()
    {
        if (id != null)
            return id;

        final int prime = 31;
        int result = 1;
        result = prime * result + ((fieldA == null) ? 0 : fieldA.hashCode());
        result = prime * result + ((fieldB == null) ? 0 : fieldB.hashCode());
        result = prime * result + ((fieldC == null) ? 0 : fieldC.hashCode());
        result = prime * result + ((fieldD == null) ? 0 : fieldD.hashCode());
        result = prime * result + ((fieldE == null) ? 0 : fieldE.hashCode());
        result = prime * result + Arrays.hashCode(fieldF);
        result = prime * result + ((fieldG == null) ? 0 : fieldG.hashCode());
        result = prime * result + Arrays.hashCode(fieldH);
        return result;
    }
}
