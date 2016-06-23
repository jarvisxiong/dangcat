package org.dangcat.persistence.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableGenerator {
    String idFieldName() default "ID";

    String tableName() default "GenerationStrategyTable";

    String valueFieldName() default "SEQUENCE";
}
