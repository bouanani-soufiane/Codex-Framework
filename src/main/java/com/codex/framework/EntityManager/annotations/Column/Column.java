package com.codex.framework.EntityManager.annotations.Column;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    String name() default "";
    boolean nullable() default true;
    int length() default 255;
    String type() default "";
    boolean unique() default false;
    boolean insertable() default true;
    boolean updatable() default true;
}