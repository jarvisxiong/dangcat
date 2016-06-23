package org.dangcat.persistence.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.dangcat.persistence.orderby.OrderByType;

@Target( { ElementType.FIELD, ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface OrderBy
{
    int index() default 0;

    OrderByType type() default OrderByType.Asc;

    String value() default "";
}
