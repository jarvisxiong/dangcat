package org.dangcat.commons.serialize.json.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonDeserialize {
    Class<? extends org.dangcat.commons.serialize.json.JsonDeserialize> value();
}
