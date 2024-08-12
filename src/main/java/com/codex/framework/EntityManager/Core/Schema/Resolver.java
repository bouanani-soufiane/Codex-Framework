package com.codex.framework.EntityManager.Core.Schema;

import com.codex.framework.EntityManager.Annotations.Column.Column;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;

public abstract class Resolver {

    private static final Map<String, String> TYPE_MAP = TypeMapper.create().typeMap();

    public static String resolveType(Field field) {
        Column columnAnnotation = field.getAnnotation(Column.class);
        if (columnAnnotation != null && !columnAnnotation.type().isEmpty()) {
            return columnAnnotation.type();
        }

        String type = TYPE_MAP.get(field.getType().getSimpleName());
        if (type != null) {
            return type;
        }

        if (isUnsupportedComplexType(field)) {
            throw new UnsupportedOperationException("Complex types like List, Set, and Map are not directly supported.");
        }

        return "VARCHAR";
    }

    private static boolean isUnsupportedComplexType(Field field) {
        return Collection.class.isAssignableFrom(field.getType()) || Map.class.isAssignableFrom(field.getType());
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