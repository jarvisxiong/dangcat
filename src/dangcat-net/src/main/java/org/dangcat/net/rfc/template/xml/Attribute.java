package org.dangcat.net.rfc.template.xml;

import org.dangcat.net.rfc.attribute.AttributeDataType;
import org.dangcat.net.rfc.template.AttributeTemplate;
import org.dangcat.net.rfc.template.IntegerAttributeTemplate;
import org.dangcat.net.rfc.template.StringAttributeTemplate;

import java.util.Map;

/**
 * 配置属性。
 *
 * @author dangcat
 */
public class Attribute {
    /**
     * 属性对象类型。
     */
    private String classType;
    /**
     * 属性类型。
     */
    private AttributeDataType dataType;
    /**
     * 加密属性。
     */
    private Boolean encrypt = Boolean.FALSE;
    /**
     * 属性长度。
     */
    private Integer length;
    /**
     * 最大值。
     */
    private Integer max;
    /**
     * 最小值。
     */
    private Integer min;
    /**
     * 属性名称。
     */
    private String name;
    /**
     * 选项列表。
     */
    private Map<Integer, String> options = null;
    /**
     * 是否进行选项校验。
     */
    private boolean optionValidate = true;
    /**
     * 属性编号。
     */
    private Integer type;

    public AttributeTemplate createAttributeTemplate() {
        AttributeTemplate attributeTemplate = AttributeTemplate.createInstance(this.getDataType());
        if (attributeTemplate == null)
            throw new RuntimeException("The attribute " + this.getName() + "(" + this.getType() + ") is not found! ");
        attributeTemplate.setType(this.getType());
        attributeTemplate.setName(this.getName());
        attributeTemplate.setEncrypt(this.getEncrypt());
        if (attributeTemplate instanceof IntegerAttributeTemplate) {
            IntegerAttributeTemplate integerAttributeTemplate = (IntegerAttributeTemplate) attributeTemplate;
            integerAttributeTemplate.setLength(this.getLength());
            integerAttributeTemplate.setMinValue(this.getMin());
            integerAttributeTemplate.setMaxValue(this.getMax());
            if (this.getOptions() != null && this.getOptions().size() > 0) {
                integerAttributeTemplate.setOptionValidate(this.isOptionValidate());
                integerAttributeTemplate.getOptions().putAll(this.getOptions());
            }
        } else if (attributeTemplate instanceof StringAttributeTemplate) {
            StringAttributeTemplate stringAttributeTemplate = (StringAttributeTemplate) attributeTemplate;
            stringAttributeTemplate.setClassType(this.getClassType());
        }
        return attributeTemplate;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public AttributeDataType getDataType() {
        return dataType;
    }

    public void setDataType(AttributeDataType dataType) {
        this.dataType = dataType;
    }

    public Boolean getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(Boolean encrypt) {
        this.encrypt = encrypt;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Integer, String> getOptions() {
        return options;
    }

    public void setOptions(Map<Integer, String> options) {
        this.options = options;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public boolean isOptionValidate() {
        return optionValidate;
    }

    public void setOptionValidate(boolean optionValidate) {
        this.optionValidate = optionValidate;
    }
}
