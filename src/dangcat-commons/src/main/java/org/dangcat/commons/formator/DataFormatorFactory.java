package org.dangcat.commons.formator;

import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.utils.ValueUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DataFormatorFactory {
    private static Map<String, Class<? extends ValueFormator>> valueFormatorMap = new HashMap<String, Class<? extends ValueFormator>>();

    static {
        valueFormatorMap.put("octets", OctetsFormator.class);
        valueFormatorMap.put("octetsVelocity", OctetsVelocityFormator.class);
        valueFormatorMap.put("timeLength", TimeLengthFormator.class);
        valueFormatorMap.put("velocity", VelocityFormator.class);
        valueFormatorMap.put("value", ValueFormator.class);
        valueFormatorMap.put("percent", PercentFormator.class);
    }

    public static void addDataFormator(String logic, Class<? extends ValueFormator> classType) {
        if (!ValueUtils.isEmpty(logic) && classType != null)
            valueFormatorMap.put(logic, classType);
    }

    public static DataFormator getDataFormator(Class<?> classType, String format) {
        DataFormator dataFormator = null;
        if (Date.class.isAssignableFrom(classType)) {
            if (!ValueUtils.isEmpty(format))
                dataFormator = new DateFormator(format);
            else
                dataFormator = DateFormator.getDateFormator(DateType.Full);
        } else if (ValueUtils.isNumber(classType)) {
            if (!ValueUtils.isEmpty(format))
                dataFormator = new NumberFormator(format);
        }
        return dataFormator;
    }

    public static ValueFormator getDataFormator(String logic) {
        ValueFormator valueFormator = null;
        Class<?> classType = valueFormatorMap.get(logic);
        if (classType == null)
            classType = ValueFormator.class;
        valueFormator = (ValueFormator) ReflectUtils.newInstance(classType);
        return valueFormator;
    }
}
