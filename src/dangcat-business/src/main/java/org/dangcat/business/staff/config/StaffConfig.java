package org.dangcat.business.staff.config;

import org.dangcat.business.config.BusinessConfig;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.persistence.entity.EntityBase;

/**
 * 操作员参数设置。
 * @author dangcat
 * 
 */
public class StaffConfig extends BusinessConfig
{
    private static final String CONFIG_NAME = "OperatorInfo";
    private static StaffConfig instance = new StaffConfig();

    private StaffConfig()
    {
        super(CONFIG_NAME);
    }

    public static StaffConfig getInstance()
    {
        return instance;
    }

    @Override
    public EntityBase createDefaultEntity()
    {
        StaffSetup staffSetup = new StaffSetup();
        staffSetup.setDefaultUseAble(Boolean.FALSE);
        staffSetup.setLogKeepMonth(3);
        staffSetup.setValidDays(365);
        return staffSetup;
    }

    public Boolean getDefaultUseAble()
    {
        return this.getBooleanValue(StaffSetup.DefaultUseAble);
    }

    public Integer getLogKeepMonth()
    {
        return this.getIntValue(StaffSetup.LogKeepMonth);
    }

    public Integer getValidDays()
    {
        return this.getIntValue(StaffSetup.ValidDays);
    }

    @Override
    public void validate(EntityBase entity) throws ServiceException
    {
    }
}
