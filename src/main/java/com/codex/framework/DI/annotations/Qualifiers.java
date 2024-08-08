package com.codex.framework.DI.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER , ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface Qualifiers {
    Qualifier[] value();
}