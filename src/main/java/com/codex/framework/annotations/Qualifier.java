package com.codex.framework.annotations;

import com.codex.testing.Pay;

import java.lang.annotation.*;

@Repeatable(Qualifiers.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER ,ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface Qualifier {
    Class<?> value();
}
