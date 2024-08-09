package com.codex.framework.EntityManager.Annotations.Id;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface GeneratedValue {
    GenerationType strategy() default GenerationType.AUTO;

    enum GenerationType {
        AUTO, IDENTITY, SEQUENCE, TABLE
    }
}