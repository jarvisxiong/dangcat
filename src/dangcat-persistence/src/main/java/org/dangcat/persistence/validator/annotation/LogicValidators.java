package org.dangcat.persistence.validator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.dangcat.persistence.validator.LogicValidator;

@Target( { ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface LogicValidators
{
    Class<? extends LogicValidator>[] value();
}
