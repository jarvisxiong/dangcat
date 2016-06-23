package org.dangcat.net.rfc.template;

import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.net.rfc.exceptions.ProtocolValidateException;

import java.util.HashMap;
import java.util.Map;

/**
 * 整数类型属性模板。
 *
 * @author dangcat
 */
public abstract class NumberAttributeTemplate<T extends Number> extends AttributeTemplate {
    /**
     * 属性长度。
     */
    private Integer length;
    /**
     * 最大值。
     */
    private T maxValue;
    /**
     * 最小值。
     */
    private T minValue;
    /**
     * 属性可选列表。
     */
    private Map<T, String> options = new HashMap<T, String>();
    /**
     * 是否进行选项校验。
     */
    private boolean optionValidate = true;

    public Integer getLength() {
        if (this.length == null || this.length < 1 || this.length > this.getMaxLength())
            return this.getMaxLength();
        return this.length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Number getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(T maxValue) {
        this.maxValue = maxValue;
    }

    public Number getMinValue() {
        return minValue;
    }

    public void setMinValue(T minValue) {
        this.minValue = minValue;
    }

    /**
     * 属性可选模板。
     */
    public Map<T, String> getOptions() {
        return this.options;
    }

    public boolean isOptionValidate() {
        return this.options != null && this.options.size() > 0 && this.optionValidate;
    }

    public void setOptionValidate(boolean optionValidate) {
        this.optionValidate = optionValidate;
    }

    @Override
    public String toString(Object value) {
        if (value instanceof Integer) {
            if (this.getOptions().containsKey(value))
                return value + " - " + this.getOptions().get(value);
        }
        return super.toString(value);
    }

    /**
     * 验证属性是否有效。
     */
    @Override
    public void validate(Object value) throws ProtocolValidateException {
        super.validate(value);

        if (this.minValue != null && ValueUtils.compare(this.minValue, value) > 0)
            throw new ProtocolValidateException(ProtocolValidateException.ATTRIBUTE_INVALID_MINVALUE, this.getFullName(), value, this.minValue);
        if (this.maxValue != null && ValueUtils.compare(this.maxValue, value) < 0)
            throw new ProtocolValidateException(ProtocolValidateException.ATTRIBUTE_INVALID_MAXVALUE, this.getFullName(), value, this.maxValue);
        if (this.isOptionValidate() && !this.getOptions().containsKey(value))
            throw new ProtocolValidateException(ProtocolValidateException.ATTRIBUTE_INVALID_OPTION, this.getFullName(), value);
    }
}
