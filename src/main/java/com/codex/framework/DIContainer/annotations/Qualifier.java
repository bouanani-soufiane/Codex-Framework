package com.codex.framework.DIContainer.annotations;

import java.lang.annotation.*;

@Repeatable(Qualifiers.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER ,ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface Qualifier {
    Class<?> value();
}
