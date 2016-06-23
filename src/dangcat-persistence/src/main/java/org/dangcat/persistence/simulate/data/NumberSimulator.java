package org.dangcat.persistence.simulate.data;

/**
 * Êý×ÖÄ£ÄâÆ÷¡£
 * @author dangcat
 * 
 */
public class NumberSimulator extends ValueSimulator
{
    private Number initValue = null;
    private Number step = null;

    public NumberSimulator(Class<?> classType)
    {
        this(classType, 0, 1);
    }

    public NumberSimulator(Class<?> classType, Number initValue, Number step)
    {
        super(classType);
        this.initValue = initValue;
        this.step = step;
    }

    protected Object createValue(int index)
    {
        Object value = null;
        Class<?> classType = this.getClassType();
        if (this.initValue != null && this.step != null)
        {
            if (Integer.class.equals(classType) || int.class.equals(classType))
                value = this.initValue.intValue() + this.step.intValue() * index;
            else if (Short.class.equals(classType) || short.class.equals(classType))
                value = (short) (this.initValue.shortValue() + this.step.shortValue() * index);
            else if (Long.class.equals(classType) || long.class.equals(classType))
                value = this.initValue.longValue() + this.step.longValue() * index;
            else if (Double.class.equals(classType) || Float.class.equals(classType) || double.class.equals(classType) || float.class.equals(classType))
                value = this.initValue.doubleValue() + this.step.doubleValue() * index;
        }
        return value;
    }

    public Number getInitValue()
    {
        return initValue;
    }

    public void setInitValue(Number initValue)
    {
        this.initValue = initValue;
    }

    public Number getStep()
    {
        return step;
    }

    public void setStep(Number step)
    {
        this.step = step;
    }
}
