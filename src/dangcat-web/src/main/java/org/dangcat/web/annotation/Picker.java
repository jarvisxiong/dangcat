package org.dangcat.web.annotation;

import org.dangcat.web.serialize.json.PickerInfo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( { ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Picker
{
    boolean canBeUnknownValue() default false;

    String displayField() default "";

    String[] filterFields() default {};

    Class<? extends PickerInfo> value();

    String valueField() default "";
}
