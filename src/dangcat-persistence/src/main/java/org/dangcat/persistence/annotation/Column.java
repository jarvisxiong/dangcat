package org.dangcat.persistence.annotation;

import org.dangcat.persistence.model.GenerationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( { ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Column
{
    int displaySize() default 0;

    String fieldName() default "";

    String format() default "";

    GenerationType generationType() default GenerationType.SEQUENCE;

    int index() default -1;

    boolean isAssociate() default false;

    boolean isAutoIncrement() default false;

    boolean isCalculate() default false;

    boolean isNullable() default true;

    boolean isPrimaryKey() default false;

    boolean isReadonly() default false;

    boolean isUnsigned() default false;

    String logic() default "";

    int scale() default 5;

    int sqlType() default 0;

    String title() default "";

    boolean visible() default true;
}
