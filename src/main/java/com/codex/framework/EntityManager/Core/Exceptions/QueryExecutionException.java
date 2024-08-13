package com.codex.framework.EntityManager.Core.Exceptions;

public class QueryExecutionException extends RuntimeException {

    public QueryExecutionException() {
        super();
    }

    public QueryExecutionException(String message) {
        super(message);
    }

    public QueryExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueryExecutionException(Throwable cause) {
        super(cause);
    }
}
