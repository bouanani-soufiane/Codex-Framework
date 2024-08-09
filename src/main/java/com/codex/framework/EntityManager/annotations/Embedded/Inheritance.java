package com.codex.framework.EntityManager.annotations.Embedded;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Inheritance {
    Strategy strategy() default Strategy.SINGLE_TABLE;

    enum Strategy {
        SINGLE_TABLE, JOINED, TABLE_PER_CLASS
    }
}