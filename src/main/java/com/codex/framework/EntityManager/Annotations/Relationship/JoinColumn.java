package com.codex.framework.EntityManager.Annotations.Relationship;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface JoinColumn {
    String name() default "";
    String referencedColumnName() default "";
}
