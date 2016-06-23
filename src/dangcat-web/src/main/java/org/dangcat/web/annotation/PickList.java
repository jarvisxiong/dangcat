package org.dangcat.web.annotation;

import org.dangcat.persistence.entity.EntityBase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PickList {
    boolean canBeUnknownValue() default false;

    String displayField() default "";

    String[] filterFields();

    boolean isTree() default false;

    String jndiName() default "";

    String pickData() default "";

    String pickerIconSrc() default "";

    Class<? extends EntityBase> resultClass();

    String valueField();
}
