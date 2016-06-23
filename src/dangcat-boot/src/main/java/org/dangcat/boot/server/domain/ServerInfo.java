package org.dangcat.boot.server.domain;

import org.dangcat.commons.formator.DateType;
import org.dangcat.commons.formator.annotation.DateStyle;
import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Table;

import java.util.Date;

@Table
public class ServerInfo extends MonitorData {
    public static final String Id = "Id";
    public static final String Ip = "Ip";
    public static final String Name = "Name";
    public static final String Status = "Status";
    public static final String Type = "Type";
    private static final long serialVersionUID = 1L;
    @Column(isPrimaryKey = true, isAutoIncrement = true, index = 0)
    private Integer id = null;

    @Column(index = 2, displaySize = 32, isNullable = false)
    private String ip = null;

    @Column(index = 4, isReadonly = true)
    @DateStyle(DateType.Second)
    private Date lastResponseTime = null;

    @Column(index = 1, displaySize = 20, isNullable = false)
    private String name = null;

    @Column(index = 5)
    private Integer status = null;

    @Column(index = 3)
    private Integer type = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getLastResponseTime() {
        return lastResponseTime;
    }

    public void setLastResponseTime(Date lastResponseTime) {
        this.lastResponseTime = lastResponseTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder info = new StringBuilder();
        if (this.getId() != null) {
            info.append(Id);
            info.append(" = ");
            info.append(this.getId());
        }
        if (this.getName() != null) {
            if (info.length() > 0)
                info.append(", ");
            info.append(Name);
            info.append(" = ");
            info.append(this.getName());
        }
        if (this.getIp() != null) {
            if (info.length() > 0)
                info.append(", ");
            info.append(Ip);
            info.append(" = ");
            info.append(this.getIp());
        }
        if (this.getType() != null) {
            if (info.length() > 0)
                info.append(", ");
            info.append(Type);
            info.append(" = ");
            info.append(this.getType());
        }
        if (this.getStatus() != null) {
            if (info.length() > 0)
                info.append(", ");
            info.append(Status);
            info.append(" = ");
            info.append(this.getStatus());
        }
        return info.toString();
    }
}
