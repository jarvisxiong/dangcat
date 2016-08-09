package org.dangcat.commons.serialize.xml;

/**
 * 参数对象
 *
 * @author dangcat
 */
public class Value implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 参数类型。
     */
    private Class<?> classType = String.class;
    /**
     * 参数值。
     */
    private Object value;

    public Class<?> getClassType() {
        return classType;
    }

    public void setClassType(Class<?> classType) {
        this.classType = classType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
