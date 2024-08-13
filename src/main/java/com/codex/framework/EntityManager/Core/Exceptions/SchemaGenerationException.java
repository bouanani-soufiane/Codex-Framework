package com.codex.framework.EntityManager.Core.Exceptions;

public class SchemaGenerationException extends RuntimeException {

    public SchemaGenerationException() {
        super();
    }

    public SchemaGenerationException(String message) {
        super(message);
    }

    public SchemaGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SchemaGenerationException(Throwable cause) {
        super(cause);
    }
}
