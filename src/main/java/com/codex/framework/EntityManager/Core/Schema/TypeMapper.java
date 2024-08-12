package com.codex.framework.EntityManager.Core.Schema;

import java.util.Map;

public record TypeMapper(Map<String, String> typeMap) {

    private static final Map<String, String> TYPE_MAP = Map.ofEntries(
            Map.entry("String", "VARCHAR"),
            Map.entry("int", "INTEGER"),
            Map.entry("Integer", "INTEGER"),
            Map.entry("long", "BIGINT"),
            Map.entry("Long", "BIGINT"),
            Map.entry("boolean", "BOOLEAN"),
            Map.entry("Boolean", "BOOLEAN"),
            Map.entry("double", "DOUBLE PRECISION"),
            Map.entry("Double", "DOUBLE PRECISION"),
            Map.entry("float", "REAL"),
            Map.entry("Float", "REAL"),
            Map.entry("char", "CHAR"),
            Map.entry("Character", "CHAR"),
            Map.entry("byte", "SMALLINT"),
            Map.entry("Byte", "SMALLINT"),
            Map.entry("short", "SMALLINT"),
            Map.entry("Short", "SMALLINT"),
            Map.entry("Date", "DATE"),
            Map.entry("Timestamp", "TIMESTAMP"),
            Map.entry("BigDecimal", "DECIMAL"),
            Map.entry("BigInteger", "NUMERIC"),
            Map.entry("LocalDate", "DATE"),
            Map.entry("LocalTime", "TIME"),
            Map.entry("LocalDateTime", "TIMESTAMP"),
            Map.entry("UUID", "UUID")
    );

    public static TypeMapper create() {
        return new TypeMapper(TYPE_MAP);
    }
}