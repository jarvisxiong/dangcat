package org.dangcat.commons.formator.annotation;

import org.dangcat.commons.formator.DateType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( { ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DateStyle
{
    DateType value() default DateType.Day;
}
