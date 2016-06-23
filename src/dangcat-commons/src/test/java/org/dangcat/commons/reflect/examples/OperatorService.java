package org.dangcat.commons.reflect.examples;

import org.dangcat.commons.reflect.Parameter;

import java.util.List;

public interface OperatorService
{
    boolean delete(@Parameter(name = "operatorInfo") OperatorInfo operatorInfo);

    LoadResult<UserInfo> load(@Parameter(name = "operatorInfo") OperatorInfo operatorInfo);

    LoadResult<OperatorInfo> query(@Parameter(name = "userInfo") UserInfo userInfo);

    List<OperatorInfo> save(@Parameter(name = "operatorInfos") List<OperatorInfo> operatorInfos);
}
