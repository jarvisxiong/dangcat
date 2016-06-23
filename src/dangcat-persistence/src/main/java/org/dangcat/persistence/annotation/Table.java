package org.dangcat.persistence.annotation;

import org.dangcat.persistence.tablename.TableName;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    String alias() default "";

    Class<? extends TableName> tableName() default TableName.class;

    String value() default "";
}
