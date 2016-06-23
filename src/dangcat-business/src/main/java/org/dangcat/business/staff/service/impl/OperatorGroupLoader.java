package org.dangcat.business.staff.service.impl;

import org.dangcat.business.staff.domain.OperatorGroup;
import org.dangcat.business.staff.domain.OperatorInfo;
import org.dangcat.framework.service.ServiceContext;
import org.dangcat.framework.service.ServicePrincipal;
import org.dangcat.persistence.entity.EntityManager;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OperatorGroupLoader
{
    private EntityManager entityManager = null;
    private OperatorInfo loginOperatorInfo = null;

    public OperatorGroupLoader(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    public Integer getCurrentGroupId()
    {
        Integer groupId = null;
        OperatorInfo loginOperatorInfo = this.getLoginOperatorInfo();
        if (loginOperatorInfo != null)
            groupId = loginOperatorInfo.getGroupId();
        return groupId;
    }

    public OperatorInfo getLoginOperatorInfo()
    {
        if (this.loginOperatorInfo == null)
        {
            ServiceContext serviceContext = ServiceContext.getInstance();
            if (serviceContext != null)
            {
                ServicePrincipal servicePrincipal = serviceContext.getServicePrincipal();
                List<OperatorInfo> operatorInfoList = this.entityManager.load(OperatorInfo.class, new String[] { OperatorInfo.No }, servicePrincipal.getNo());
                if (operatorInfoList != null && operatorInfoList.size() > 0)
                    this.loginOperatorInfo = operatorInfoList.get(0);
            }
        }
        return this.loginOperatorInfo;
    }

    /**
     * 当前用户的所有子组列表。
     */
    public Integer[] loadMemberIds()
    {
        Integer[] groupIds = null;
        Map<Integer, String> operatorGroupMap = this.loadMembers(null);
        if (operatorGroupMap != null)
            groupIds = operatorGroupMap.keySet().toArray(new Integer[0]);
        return groupIds;
    }

    public Map<Integer, String> loadMembers(Integer parentId)
    {
        Map<Integer, String> operatorGroupMap = new LinkedHashMap<Integer, String>();
        if (parentId == null)
            parentId = this.getCurrentGroupId();
        List<OperatorGroup> operatorGroupList = this.entityManager.load(OperatorGroup.class);
        if (operatorGroupList != null && operatorGroupList.size() > 0)
        {
            if (parentId != null)
                this.loadMembers(operatorGroupMap, operatorGroupList, parentId);
            else
            {
                for (OperatorGroup operatorGroup : operatorGroupList)
                    operatorGroupMap.put(operatorGroup.getId(), operatorGroup.getName());
            }
        }
        return operatorGroupMap;
    }

    private void loadMembers(Map<Integer, String> operatorGroupMap, List<OperatorGroup> operatorGroupList, Integer parentId)
    {
        if (operatorGroupList != null)
        {
            for (OperatorGroup operatorGroup : operatorGroupList)
            {
                if (parentId.equals(operatorGroup.getId()))
                    operatorGroupMap.put(operatorGroup.getId(), operatorGroup.getName());
                else if (parentId.equals(operatorGroup.getParentId()) && !operatorGroupMap.containsKey(operatorGroup.getId()))
                    this.loadMembers(operatorGroupMap, operatorGroupList, operatorGroup.getId());
            }
        }
    }
}
