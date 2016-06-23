package org.dangcat.business.staff.config;

import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Table;
import org.dangcat.persistence.entity.EntityBase;

/**
 * 操作员参数设置。
 *
 * @author dangcat
 */
@Table
public class StaffSetup extends EntityBase {
    public static final String DefaultUseAble = "DefaultUseAble";
    public static final String LogKeepMonth = "LogKeepMonth";
    public static final String ValidDays = "ValidDays";
    private static final long serialVersionUID = 1L;
    /**
     * 新建账户默认开启否。
     */
    @Column(isNullable = false)
    private Boolean defaultUseAble = null;
    /**
     * 操作日志保留周期。
     */
    @Column
    private Integer logKeepMonth = null;
    /**
     * 账户有效天数。
     */
    @Column
    private Integer validDays = null;

    public Boolean getDefaultUseAble() {
        return defaultUseAble;
    }

    public void setDefaultUseAble(Boolean defaultUseAble) {
        this.defaultUseAble = defaultUseAble;
    }

    public Integer getLogKeepMonth() {
        return logKeepMonth;
    }

    public void setLogKeepMonth(Integer logKeepMonth) {
        this.logKeepMonth = logKeepMonth;
    }

    public Integer getValidDays() {
        return validDays;
    }

    public void setValidDays(Integer validDays) {
        this.validDays = validDays;
    }
}
