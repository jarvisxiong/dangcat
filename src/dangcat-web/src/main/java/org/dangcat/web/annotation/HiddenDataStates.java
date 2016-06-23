package org.dangcat.web.annotation;

import org.dangcat.persistence.model.DataState;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( { ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface HiddenDataStates
{
    DataState[] value();
}
