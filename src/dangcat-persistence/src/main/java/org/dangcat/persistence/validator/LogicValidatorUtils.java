package org.dangcat.persistence.validator;

import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.model.Column;

/**
 * 逻辑校验接口。
 * @author dangcat
 * 
 */
public abstract class LogicValidatorUtils extends DataValidator
{
    private static final String LOGICTEST = "logictest";

    public static LogicValidator creatInstance(Class<?> classType, Class<?> entityClass, Column column)
    {
        LogicValidator logicValidator = null;
        if (LogicValidator.class.isAssignableFrom(classType))
            logicValidator = (LogicValidator) ReflectUtils.newInstance(classType, new Class<?>[] { Class.class, Column.class }, new Object[] { entityClass, column });
        return logicValidator;
    }

    public static void disable()
    {
        Environment.setModuleEnabled(LOGICTEST, false);
    }

    public static LogicValidator getExtendLogicValidator(Class<?> logicClassType, Class<?> entityClass, Column column)
    {
        LogicValidator logicValidator = null;
        String className = System.getProperty(logicClassType.getName());
        if (!ValueUtils.isEmpty(className))
        {
            Class<?> classType = ReflectUtils.loadClass(className);
            if (classType != null)
                logicValidator = creatInstance(classType, entityClass, column);
        }
        return logicValidator;
    }

    public static boolean isEnabled()
    {
        return Environment.isModuleEnabled(LOGICTEST, Boolean.TRUE);
    }
}
