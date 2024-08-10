package com.codex.framework.EntityManager.Core.Shema;

import com.codex.framework.EntityManager.Annotations.Column.Column;

import java.lang.reflect.Field;

public class Resolver {

    private Resolver(){}

    public static String resolveType( Field field) {
        String type;

        if (field.isAnnotationPresent(Column.class) && !field.getAnnotation(Column.class).type().isEmpty()) {
            return field.getAnnotation(Column.class).type();
        } else if (field.getType().isEnum()) {
            return resolveEnum(field.getType());
        } else {
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
                    type = "DOUBLE";
                    break;
                case "float":
                case "Float":
                    type = "FLOAT";
                    break;
                case "char":
                case "Character":
                    type = "CHAR";
                    break;
                case "byte":
                case "Byte":
                    type = "TINYINT";
                    break;
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
                    throw new UnsupportedOperationException("Complex types like List, Set and Map are not directly supported.");
                default:
                    type = "VARCHAR";
            }
            return type;
        }
    }

    public static String resolveEnum(Class<?> type) {
        StringBuilder enumValues = new StringBuilder("ENUM(");

        for (Object enumConstant : type.getEnumConstants()) {
            enumValues.append("'").append(enumConstant.toString()).append("', ");
        }
        if (enumValues.length() > 5) {
            enumValues.setLength(enumValues.length() - 2);
        }
        enumValues.append(")");

        return enumValues.toString();
    }

}
