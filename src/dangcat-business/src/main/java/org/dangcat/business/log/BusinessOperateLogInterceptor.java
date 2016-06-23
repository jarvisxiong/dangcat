package org.dangcat.business.log;

import org.apache.log4j.Logger;
import org.dangcat.boot.security.impl.LoginUser;
import org.dangcat.boot.service.impl.EntityBatchServiceImpl;
import org.dangcat.business.exceptions.BusinessException;
import org.dangcat.business.staff.domain.OperateLog;
import org.dangcat.commons.reflect.MethodInfo;
import org.dangcat.commons.reflect.ParamInfo;
import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.framework.service.ServiceContext;
import org.dangcat.framework.service.ServicePrincipal;
import org.dangcat.framework.service.impl.ServiceInfo;
import org.dangcat.framework.service.interceptor.AfterInterceptor;
import org.dangcat.persistence.batch.EntityBatchStorer;
import org.dangcat.persistence.entity.EntityBase;
import org.dangcat.persistence.entity.EntityField;
import org.dangcat.persistence.entity.EntityHelper;
import org.dangcat.persistence.entity.EntityMetaData;

import java.util.Collection;
import java.util.HashSet;

/**
 * 业务操作日志。
 *
 * @author fanst174766
 */
public class BusinessOperateLogInterceptor implements AfterInterceptor {
    protected static final Logger logger = Logger.getLogger(BusinessOperateLogInterceptor.class);
    private static final String COLON = ": ";
    private static final String EQUALS = "=";
    private static final String ERROR = "Error";
    private static final String FIELDNAME_NAME = "Name";
    private static final String FIELDNAME_NO = "No";
    private static final String FLAG1 = "/";
    private static final Collection<String> ignoreMethodNames = new HashSet<String>();
    private static final String INVOKE = "Invoke ";
    private static final String PARAMS = "Params: ";
    private static final String RESULT = "Result: ";
    private static final String RETURN = "Return: ";
    private static final String SEPARATOR = ", ";
    private static final String SPACE = " ";
    private static final String SUCCESS = "Success";
    private static final String TIME_COST = "TimeCost";
    private static final String USER = "User ";

    static {
        ignoreMethodNames.add("query");
        ignoreMethodNames.add("load");
    }

    @Override
    public void afterInvoke(Object service, ServiceContext serviceContext, MethodInfo methodInfo, Object[] args, Object result) {
        this.log(service, serviceContext, methodInfo, args, result);

        if (methodInfo.getValue() == null)
            return;

        ServicePrincipal servicePrincipal = serviceContext.getServicePrincipal();
        if (servicePrincipal == null)
            return;

        LoginUser loginUser = servicePrincipal.getParam(LoginUser.class);
        if (loginUser == null || loginUser.getId() == null)
            return;

        if (result instanceof EntityBase) {
            EntityBase entityBase = (EntityBase) result;
            if (entityBase.hasError())
                return;
        }

        if (ignoreMethodNames.contains(methodInfo.getName()) || methodInfo.getMethod().getAnnotation(IgnoreLog.class) != null)
            return;

        this.createOperateLog(loginUser, servicePrincipal, methodInfo, args, result);
    }

    private void createOperateLog(LoginUser loginUser, ServicePrincipal servicePrincipal, MethodInfo methodInfo, Object[] args, Object result) {
        OperateLog operateLog = new OperateLog();
        operateLog.setOperatorId(loginUser.getId());
        operateLog.setMethodId(methodInfo.getValue());
        operateLog.setIpAddress(servicePrincipal.getClientIp());
        if (result instanceof ServiceException) {
            ServiceException serviceException = (ServiceException) result;
            operateLog.setErrorCode(serviceException.getMessageId());
        } else if (result instanceof Exception)
            operateLog.setErrorCode(BusinessException.INVOKE_ERROR);
        else
            operateLog.setErrorCode(OperateLog.SUCCESS);
        this.readRemark(operateLog, methodInfo, args, result);

        EntityBatchStorer entityBatchStorer = EntityBatchServiceImpl.getInstance().getEntityBatchStorer();
        entityBatchStorer.save(operateLog);
    }

    private void log(Object service, ServiceContext serviceContext, MethodInfo methodInfo, Object[] args, Object result) {
        StringBuilder info = new StringBuilder();
        ServicePrincipal servicePrincipal = serviceContext.getServicePrincipal();
        if (servicePrincipal != null) {
            LoginUser loginUser = servicePrincipal.getParam(LoginUser.class);
            if (loginUser != null) {
                info.append(USER);
                info.append(loginUser.getNo());
                info.append(SPACE);
            }
        }

        ServiceInfo serviceInfo = serviceContext.getServiceInfo();
        info.append(INVOKE);
        info.append(serviceInfo.getJndiName());
        info.append(FLAG1);
        info.append(methodInfo.getName());
        info.append(COLON);

        String params = this.readRemark(methodInfo, args);
        if (!ValueUtils.isEmpty(params)) {
            info.append(PARAMS);
            info.append(params);
        }

        String returnText = null;
        if (result instanceof Exception) {
            Exception exception = (Exception) result;
            returnText = exception.getMessage();
        } else if (result != null)
            returnText = this.readRemark(result);
        if (!ValueUtils.isEmpty(returnText)) {
            if (!ValueUtils.isEmpty(params))
                info.append(SEPARATOR);
            info.append(RETURN);
            info.append(returnText);
            info.append(SEPARATOR);
        }

        info.append(RESULT);
        if (result instanceof EntityBase) {
            EntityBase entityBase = (EntityBase) result;
            if (entityBase.hasError())
                info.append(ERROR);
            else
                info.append(SUCCESS);
        } else
            info.append(SUCCESS);

        long timeCost = DateUtils.currentTimeMillis() - serviceContext.getBeginTime().getTime();
        if (timeCost > 0) {
            info.append(SPACE);
            info.append(TIME_COST);
            info.append(COLON);
            info.append(timeCost);
        }
        logger.info(info);
    }

    private String readRemark(MethodInfo methodInfo, Object[] args) {
        StringBuilder remark = null;
        if (args != null && args.length == 1 && args[0] != null) {
            if (args[0] instanceof Integer) {
                ParamInfo paramInfo = methodInfo.getParamInfoList().get(0);
                remark = new StringBuilder();
                remark.append(paramInfo.getName());
                remark.append(EQUALS);
                remark.append(args[0].toString());
            } else
                return this.readRemark(args[0]);
        }
        return remark == null ? null : remark.toString();
    }

    private String readRemark(Object entity) {
        if (ReflectUtils.isConstClassType(entity.getClass())) {
            String result = ValueUtils.toString(entity);
            if (result != null && result.length() > 100 && !logger.isDebugEnabled())
                result = result.substring(0, 100);
            return result;
        }

        StringBuilder remark = null;
        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entity);
        if (entityMetaData != null) {
            remark = new StringBuilder();
            String[] primaryKeyNames = null;
            EntityField noField = entityMetaData.getEntityField(FIELDNAME_NO);
            EntityField nameField = entityMetaData.getEntityField(FIELDNAME_NAME);
            if (noField != null && nameField != null)
                primaryKeyNames = new String[]{FIELDNAME_NO, FIELDNAME_NAME};
            else if (noField != null)
                primaryKeyNames = new String[]{FIELDNAME_NO};
            else if (nameField != null)
                primaryKeyNames = new String[]{FIELDNAME_NAME};

            if (primaryKeyNames == null || primaryKeyNames.length == 0)
                primaryKeyNames = entityMetaData.getPrimaryKeyNames();

            if (primaryKeyNames != null && primaryKeyNames.length > 0) {
                for (String primaryKeyName : primaryKeyNames) {
                    EntityField entityField = entityMetaData.getEntityField(primaryKeyName);
                    if (entityField != null) {
                        Object value = entityField.getValue(entity);
                        if (value != null) {
                            if (remark.length() > 0)
                                remark.append(SEPARATOR);
                            remark.append(primaryKeyName);
                            remark.append(EQUALS);
                            remark.append(ValueUtils.toString(value));
                        }
                    }
                }
            }
        }
        return remark == null ? null : remark.toString();
    }

    private void readRemark(OperateLog operateLog, MethodInfo methodInfo, Object[] args, Object result) {
        String remark = this.readRemark(methodInfo, args);
        if (result instanceof Boolean && Boolean.FALSE.equals(result))
            operateLog.setErrorCode(BusinessException.DELETE_ERROR);
        operateLog.setRemark(remark);
    }
}
