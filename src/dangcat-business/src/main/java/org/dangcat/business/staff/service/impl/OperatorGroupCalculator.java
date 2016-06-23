package org.dangcat.business.staff.service.impl;

import org.apache.log4j.Logger;
import org.dangcat.business.staff.domain.OperatorGroup;
import org.dangcat.business.staff.service.OperatorGroupService;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.framework.service.impl.ServiceFactory;
import org.dangcat.persistence.calculate.Calculator;

import java.util.Collection;
import java.util.Map;

public class OperatorGroupCalculator implements Calculator {
    public static final Logger logger = Logger.getLogger(OperatorGroupCalculator.class);

    public static Map<Integer, String> getOperatorGroupMap() {
        Map<Integer, String> operatorGroupMap = null;
        try {
            OperatorGroupService operatorGroupService = ServiceFactory.getServiceLocator().getService(OperatorGroupService.class);
            operatorGroupMap = operatorGroupService.select(null);
        } catch (ServiceException e) {
            logger.error(e, e);
        }
        return operatorGroupMap;
    }

    @Override
    public void calculate(Collection<?> entityCollection) {
        Map<Integer, String> operatorGroupMap = getOperatorGroupMap();
        if (operatorGroupMap != null) {
            for (Object entity : entityCollection) {
                OperatorGroup operatorGroup = (OperatorGroup) entity;
                operatorGroup.setParentName(operatorGroupMap.get(operatorGroup.getParentId()));
            }
        }
    }

    @Override
    public void calculate(Object entity) {
        Map<Integer, String> operatorGroupMap = getOperatorGroupMap();
        if (operatorGroupMap != null) {
            OperatorGroup operatorGroup = (OperatorGroup) entity;
            operatorGroup.setParentName(operatorGroupMap.get(operatorGroup.getParentId()));
        }
    }
}
