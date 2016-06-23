package org.dangcat.persistence.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.dangcat.commons.database.DatabaseType;

@Target( { ElementType.ANNOTATION_TYPE, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Sql
{
    DatabaseType databaseType() default DatabaseType.Default;

    String delimiter() default ";";

    String name() default org.dangcat.persistence.sql.Sql.QUERY;

    String value();
}
