package com.codex.framework.DIContainer.exceptions;

public class NoSuchBeanException extends RuntimeException {
    public NoSuchBeanException() {
        super();
    }
    public NoSuchBeanException(String message) {
        super(message);
    }

    public NoSuchBeanException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchBeanException(Throwable cause) {
        super(cause);
    }
}
