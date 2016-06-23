package org.dangcat.persistence.annotation;

import org.dangcat.persistence.entity.JoinType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( { ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinTable
{
    JoinColumn[] joinColumns();

    String joinTableAlias() default "";

    String joinTableName() default "";

    JoinType joinType() default JoinType.Inner;

    String tableAlias() default "";

    String tableName() default "";
}
