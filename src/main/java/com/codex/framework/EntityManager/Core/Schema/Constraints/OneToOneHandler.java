package com.codex.framework.EntityManager.Core.Schema.Constraints;

import com.codex.framework.EntityManager.Annotations.Id.ID;
import com.codex.framework.EntityManager.Annotations.Relationship.JoinColumn;
import com.codex.framework.EntityManager.Core.Schema.SchemaGenerator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class OneToOneHandler {

    public static List<String> collectConstraints(Class<?> entity) {
        List<String> queries = new ArrayList<>();

        for (Field field : entity.getDeclaredFields()) {
            if (field.isAnnotationPresent(com.codex.framework.EntityManager.Annotations.Relationship.OneToOne.class)) {
                String tableName = SchemaGenerator.getTableName(entity);
                String fieldName = resolveFieldName(field);
                String referencedTable = SchemaGenerator.getTableName(field.getType());
                String referencedPrimaryKey = getPrimaryKeyColumnName(field.getType());

                String query = String.format(
                        "ALTER TABLE %s ADD COLUMN %s BIGINT, ADD CONSTRAINT fk_%s FOREIGN KEY (%s) REFERENCES %s(%s);",
                        tableName, fieldName, fieldName, fieldName, referencedTable, referencedPrimaryKey
                );

                queries.add(query);
            }
        }

        return queries;
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
}

