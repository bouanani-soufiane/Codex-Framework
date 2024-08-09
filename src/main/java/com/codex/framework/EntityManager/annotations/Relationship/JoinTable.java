package com.codex.framework.EntityManager.annotations.Relationship;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinTable {
    String name() default "";
    String joinColumns() default "";
    String inverseJoinColumns() default "";
}
