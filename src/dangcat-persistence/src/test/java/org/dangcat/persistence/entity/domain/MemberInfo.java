package org.dangcat.persistence.entity.domain;

import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Table;
import org.dangcat.persistence.entity.EntityBase;

import java.util.Date;

@Table
public class MemberInfo extends EntityBase {
    private static final long serialVersionUID = 1L;

    @Column
    private Date expireTime = null;

    @Column(isPrimaryKey = true)
    private Integer id = null;

    @Column(displaySize = 128)
    private String no = null;

    @Column(displaySize = 32)
    private String password = null;

    @Column(index = 13, isNullable = false)
    private Boolean useAble = null;

    public MemberInfo() {
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNo() {
        return this.no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getUseAble() {
        return useAble;
    }

    public void setUseAble(Boolean useAble) {
        this.useAble = useAble;
    }
}