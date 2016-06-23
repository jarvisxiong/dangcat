package org.dangcat.persistence.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Relation {
    boolean associateDelete() default true;

    boolean associateLoad() default true;

    boolean associateSave() default true;

    String[] childFieldNames() default {};

    String[] parentFieldNames() default {};

    String sortBy() default "";
}
