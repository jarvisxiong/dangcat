package org.dangcat.business.staff.service.impl;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dangcat.business.staff.domain.OperatorInfo;
import org.dangcat.persistence.calculate.Calculator;

public class OperatorInfoCalculator implements Calculator
{
    public static final Logger logger = Logger.getLogger(OperatorInfoCalculator.class);

    @Override
    public void calculate(Collection<?> entityCollection)
    {
        Map<Integer, String> operatorGroupMap = OperatorGroupCalculator.getOperatorGroupMap();
        Map<Integer, String> roleInfoMap = RoleInfoCalculator.getRoleInfoMap();
        for (Object entity : entityCollection)
        {
            OperatorInfo operatorInfo = (OperatorInfo) entity;
            if (operatorGroupMap != null)
                operatorInfo.setGroupName(operatorGroupMap.get(operatorInfo.getGroupId()));
            if (roleInfoMap != null)
                operatorInfo.setRoleName(roleInfoMap.get(operatorInfo.getRoleId()));
        }
    }

    @Override
    public void calculate(Object entity)
    {
        OperatorInfo operatorInfo = (OperatorInfo) entity;

        Map<Integer, String> operatorGroupMap = OperatorGroupCalculator.getOperatorGroupMap();
        if (operatorGroupMap != null)
            operatorInfo.setGroupName(operatorGroupMap.get(operatorInfo.getGroupId()));

        Map<Integer, String> roleInfoMap = RoleInfoCalculator.getRoleInfoMap();
        if (roleInfoMap != null)
            operatorInfo.setRoleName(roleInfoMap.get(operatorInfo.getRoleId()));
    }
}
