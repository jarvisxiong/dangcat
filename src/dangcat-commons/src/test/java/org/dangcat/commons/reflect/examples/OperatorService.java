package org.dangcat.commons.reflect.examples;

import java.util.List;

import org.dangcat.commons.reflect.Parameter;

public interface OperatorService
{
    public boolean delete(@Parameter(name = "operatorInfo") OperatorInfo operatorInfo);

    public LoadResult<UserInfo> load(@Parameter(name = "operatorInfo") OperatorInfo operatorInfo);

    public LoadResult<OperatorInfo> query(@Parameter(name = "userInfo") UserInfo userInfo);

    public List<OperatorInfo> save(@Parameter(name = "operatorInfos") List<OperatorInfo> operatorInfos);
}
