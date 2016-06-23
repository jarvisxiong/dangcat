package org.dangcat.persistence.entity.domain;

import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Relation;
import org.dangcat.persistence.annotation.Table;

import java.util.Date;

@Table
public class TeacherInfo extends PersonInfo {
    private static final long serialVersionUID = 1L;

    @Relation(parentFieldNames = {Id}, childFieldNames = {Id})
    private MemberInfo memberInfo = new MemberInfo();

    @Column(isCalculate = true)
    public Date getExpireTime() {
        return this.memberInfo.getExpireTime();
    }

    public void setExpireTime(Date expireTime) {
        this.memberInfo.setExpireTime(expireTime);
    }

    @Column(displaySize = 128, isCalculate = true)
    public String getNo() {
        return this.memberInfo.getNo();
    }

    public void setNo(String no) {
        this.memberInfo.setNo(no);
    }

    @Column(displaySize = 32, isCalculate = true)
    public String getPassword() {
        return this.memberInfo.getPassword();
    }

    public void setPassword(String password) {
        this.memberInfo.setPassword(password);
    }

    @Column(index = 13, isNullable = false, isCalculate = true)
    public Boolean getUseAble() {
        return this.memberInfo.getUseAble();
    }

    public void setUseAble(Boolean useAble) {
        this.memberInfo.setUseAble(useAble);
    }
}
