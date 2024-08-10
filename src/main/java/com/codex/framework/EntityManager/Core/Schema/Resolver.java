package com.codex.framework.EntityManager.Core.Schema;

import com.codex.framework.EntityManager.Annotations.Column.Column;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class Resolver {

     static String resolveType(Field field) {
        Column columnAnnotation = field.getAnnotation(Column.class);
        String type = columnAnnotation != null && !columnAnnotation.type().isEmpty()
                ? columnAnnotation.type()
                : resolveDefaultType(field);

        return type;
    }

     static String resolveDefaultType(Field field) {
        String type;
        switch (field.getType().getSimpleName()) {
            case "String":
                type = "VARCHAR";
                break;
            case "int":
            case "Integer":
                type = "INTEGER";
                break;
            case "long":
            case "Long":
                type = "BIGINT";
                break;
            case "boolean":
            case "Boolean":
                type = "BOOLEAN";
                break;
            case "double":
            case "Double":
                type = "DOUBLE PRECISION";
                break;
            case "float":
            case "Float":
                type = "REAL";
                break;
            case "char":
            case "Character":
                type = "CHAR";
                break;
            case "byte":
            case "Byte":
            case "short":
            case "Short":
                type = "SMALLINT";
                break;
            case "Date":
                type = "DATE";
                break;
            case "Timestamp":
                type = "TIMESTAMP";
                break;
            case "BigDecimal":
                type = "DECIMAL";
                break;
            case "BigInteger":
                type = "NUMERIC";
                break;
            case "LocalDate":
                type = "DATE";
                break;
            case "LocalTime":
                type = "TIME";
                break;
            case "LocalDateTime":
                type = "TIMESTAMP";
                break;
            case "UUID":
                type = "UUID";
                break;
            case "List":
            case "Set":
            case "Map":
                throw new UnsupportedOperationException("Complex types like List, Set, and Map are not directly supported.");
            default:
                type = "VARCHAR";
        }
        return type;
    }

    public static String resolveEnum(Class<?> enumType) {
        StringBuilder enumValues = new StringBuilder("(");

        for (Object enumConstant : enumType.getEnumConstants()) {
            enumValues.append("'").append(enumConstant.toString()).append("', ");
        }

        if (enumValues.length() > 1) {
            enumValues.setLength(enumValues.length() - 2);
        }
        enumValues.append(")");

        return enumValues.toString();
    }

    public static void CreateEnum(Class<?> enumType, Connection conn) throws SQLException {
        String typeName = enumType.getSimpleName().toLowerCase();
        String query = String.format(
                "DO $$\n" +
                        "BEGIN\n" +
                        "    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = '%s') THEN\n" +
                        "        CREATE TYPE %s AS ENUM %s;\n" +
                        "    END IF;\n" +
                        "END $$;",
                typeName, enumType.getSimpleName(), resolveEnum(enumType)
        );

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
            System.out.println("Enum " + enumType.getSimpleName() + " created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating Enum " + enumType.getSimpleName()
                    + " with query: " + query
                    + "\nError: " + e.getMessage());
            throw e;
        }
    }

    public static String resolveTypeWihtLenght(String type, int length, int scale) {
        if ("VARCHAR".equals(type) || "CHAR".equals(type)) {
            return String.format("%s(%d)", type, length);
        } else if ("NUMERIC".equals(type)) {
            return String.format("%s(%d,%d)", type, length, scale);
        }
        return type;
    }
}
