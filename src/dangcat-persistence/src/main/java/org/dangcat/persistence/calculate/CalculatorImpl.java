package org.dangcat.persistence.calculate;

import org.dangcat.commons.reflect.ReflectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class CalculatorImpl implements Calculator
{
    private Collection<Calculator> calculators = new ArrayList<Calculator>();
    /** º∆À„∆˜≈‰÷√°£ */
    private Collection<Class<?>> calculatorTypes = new HashSet<Class<?>>();

    public void add(Calculator calculator)
    {
        if (calculator != null)
        {
            this.calculators.add(calculator);
            this.calculatorTypes.add(calculator.getClass());
        }
    }

    public void add(Class<?> classType)
    {
        if (Calculator.class.isAssignableFrom(classType))
            this.calculatorTypes.add(classType);
    }

    @Override
    public void calculate(Collection<?> entityCollection)
    {
        for (Calculator calculator : this.calculators)
            calculator.calculate(entityCollection);
    }

    @Override
    public void calculate(Object entity)
    {
        for (Calculator calculator : this.calculators)
            calculator.calculate(entity);
    }

    public Collection<Calculator> getAll()
    {
        return calculators;
    }

    public Collection<Class<?>> getAllTypes()
    {
        return calculatorTypes;
    }

    public void initialize()
    {
        this.calculators.clear();
        for (Class<?> classType : this.calculatorTypes)
        {
            Object instance = ReflectUtils.newInstance(classType);
            if (instance instanceof Calculator)
                this.calculators.add((Calculator) instance);
        }
    }

    public void putAll(Collection<Calculator> calculators)
    {
        if (calculators != null)
        {
            for (Calculator calculator : calculators)
                this.add(calculator);
        }
    }

    public void putTypes(Collection<Class<?>> calculatorTypes)
    {
        this.calculatorTypes.addAll(calculatorTypes);
    }

    public int size()
    {
        return this.calculators.size();
    }
}
