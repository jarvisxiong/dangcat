package org.dangcat.persistence.entity.domain;

import java.util.Date;

import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Relation;
import org.dangcat.persistence.annotation.Table;

@Table
public class TeacherInfo extends PersonInfo
{
    private static final long serialVersionUID = 1L;

    @Relation(parentFieldNames = { Id }, childFieldNames = { Id })
    private MemberInfo memberInfo = new MemberInfo();

    @Column(isCalculate = true)
    public Date getExpireTime()
    {
        return this.memberInfo.getExpireTime();
    }

    @Column(displaySize = 128, isCalculate = true)
    public String getNo()
    {
        return this.memberInfo.getNo();
    }

    @Column(displaySize = 32, isCalculate = true)
    public String getPassword()
    {
        return this.memberInfo.getPassword();
    }

    @Column(index = 13, isNullable = false, isCalculate = true)
    public Boolean getUseAble()
    {
        return this.memberInfo.getUseAble();
    }

    public void setExpireTime(Date expireTime)
    {
        this.memberInfo.setExpireTime(expireTime);
    }

    public void setNo(String no)
    {
        this.memberInfo.setNo(no);
    }

    public void setPassword(String password)
    {
        this.memberInfo.setPassword(password);
    }

    public void setUseAble(Boolean useAble)
    {
        this.memberInfo.setUseAble(useAble);
    }
}
