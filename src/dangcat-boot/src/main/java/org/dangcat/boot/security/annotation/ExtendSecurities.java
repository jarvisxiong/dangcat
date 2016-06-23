package org.dangcat.boot.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.dangcat.boot.security.impl.LoginServiceBase;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtendSecurities
{
    Class<? extends LoginServiceBase>[] value();
}
