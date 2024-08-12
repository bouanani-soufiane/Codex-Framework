package com.codex.framework.EntityManager.Core.Schema.Constraints;

import com.codex.framework.EntityManager.Annotations.Id.ID;
import com.codex.framework.EntityManager.Annotations.Relationship.JoinColumn;
import com.codex.framework.EntityManager.Core.Schema.SchemaGenerator;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ManyToOneHandler {

    public static void addConstraints(Class<?> entity, Connection conn) throws SQLException {
        for (Field field : entity.getDeclaredFields()) {
            if (field.isAnnotationPresent(com.codex.framework.EntityManager.Annotations.Relationship.ManyToOne.class)) {
                String tableName = SchemaGenerator.getTableName(entity);
                String fieldName = resolveFieldName(field);
                String referencedTable = SchemaGenerator.getTableName(field.getType());
                String referencedPrimaryKey = getPrimaryKeyColumnName(field.getType());

                String query = String.format(
                        "ALTER TABLE %s ADD COLUMN %s BIGINT, ADD CONSTRAINT fk_%s FOREIGN KEY (%s) REFERENCES %s(%s);",
                        tableName, fieldName, fieldName, fieldName, referencedTable, referencedPrimaryKey
                );

                executeQuery(query, conn);
            }
        }
    }

    private static String resolveFieldName(Field field) {
        JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
        return (joinColumn != null && !joinColumn.name().isEmpty()) ? joinColumn.name() : field.getName();
    }

    private static String getPrimaryKeyColumnName(Class<?> entityClass) {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(ID.class)) {
                ID idAnnotation = field.getAnnotation(ID.class);
                return (idAnnotation != null && !idAnnotation.name().isEmpty()) ? idAnnotation.name() : field.getName();
            }
        }
        throw new IllegalArgumentException("No @Id annotation found in " + entityClass.getSimpleName());
    }

    private static void executeQuery(String query, Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
            System.out.println("Executed query: " + query);
        } catch (SQLException e) {
            System.err.println("Error executing query: " + query + "\nError: " + e.getMessage());
            throw e;
        }
    }
}
