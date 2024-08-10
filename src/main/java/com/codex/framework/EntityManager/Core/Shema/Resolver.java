package com.codex.framework.EntityManager.Core.Shema;

import com.codex.framework.EntityManager.Annotations.Column.Column;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class Resolver {

     static String resolveType( Field field) {
        String type;

        if (field.isAnnotationPresent(Column.class) && !field.getAnnotation(Column.class).type().isEmpty()) {
            return field.getAnnotation(Column.class).type();
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

    static String resolveEnum(Class<?> type) {
        StringBuilder enumValues = new StringBuilder("(");

        for (Object enumConstant : type.getEnumConstants()) {
            enumValues.append("'").append(enumConstant.toString()).append("', ");
        }
        if (enumValues.length() > 5) {
            enumValues.setLength(enumValues.length() - 2);
        }
        enumValues.append(")");

        return enumValues.toString();
    }

    static void getOrCreateEnum ( Class<?> type , Connection conn ) throws SQLException {

        String query = "DO $$\n" +
                "BEGIN\n" +
                "    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = '" + type.getSimpleName().toLowerCase() + "') THEN\n" +
                "        CREATE TYPE " + type.getSimpleName() + " AS ENUM " + resolveEnum(type) + ";\n" +
                "    END IF;\n" +
                "END $$;";

        try(Statement stmt = conn.createStatement()){
            stmt.executeUpdate(query);
            System.out.println("Enum " + type + " created successfully.");
        }catch (SQLException e){
            System.err.println("error creating Enum" + type
                    +" for query" + query
                    + "\n : " + e.getMessage());
        }

    }

}
