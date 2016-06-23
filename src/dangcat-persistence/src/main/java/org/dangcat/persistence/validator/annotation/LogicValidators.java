package org.dangcat.persistence.validator.annotation;

import org.dangcat.persistence.validator.LogicValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( { ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface LogicValidators
{
    Class<? extends LogicValidator>[] value();
}
