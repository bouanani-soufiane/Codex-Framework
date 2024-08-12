package com.codex.framework.EntityManager.Core.Schema.Constraints;

import com.codex.framework.EntityManager.Annotations.Id.ID;
import com.codex.framework.EntityManager.Annotations.Relationship.JoinColumn;
import com.codex.framework.EntityManager.Annotations.Relationship.JoinTable;
import com.codex.framework.EntityManager.Annotations.Relationship.ManyToMany;
import com.codex.framework.EntityManager.Core.Schema.SchemaGenerator;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ManyToManyHandler {

    public static List<String> collectConstraints(Class<?> entity) {
        List<String> queries = new ArrayList<>();

        for (Field field : entity.getDeclaredFields()) {
            if (field.isAnnotationPresent(ManyToMany.class)) {
                ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
                JoinTable joinTable = field.getAnnotation(JoinTable.class);

                if (joinTable != null) {
                    String joinTableName = joinTable.name();
                    String[] joinColumnNames = extractColumnNames(joinTable.joinColumns());
                    String[] inverseJoinColumnNames = extractColumnNames(joinTable.inverseJoinColumns());
                    Class<?> fieldClass = getGenericType(field);

                    String query = buildJoinTableQuery(joinTableName, joinColumnNames, inverseJoinColumnNames , entity ,  fieldClass);
                    queries.add(query);
                }
            }
        }

        return queries;
    }

    private static String[] extractColumnNames(JoinColumn[] joinColumns) {
        String[] columnNames = new String[joinColumns.length];
        for (int i = 0; i < joinColumns.length; i++) {
            columnNames[i] = joinColumns[i].name();
        }
        return columnNames;
    }

    private static String buildJoinTableQuery(String joinTableName, String[] joinColumnNames, String[] inverseJoinColumnNames , Class<?> entity , Class<?> field) {
        StringBuilder query = new StringBuilder(String.format("CREATE TABLE IF NOT EXISTS %s (\n", joinTableName));

        for (String columnName : joinColumnNames) {
            query.append(String.format("\t%s BIGINT,\n", columnName));
        }

        for (String columnName : inverseJoinColumnNames) {
            query.append(String.format("\t%s BIGINT,\n", columnName));
        }

        query.append(String.format("\tPRIMARY KEY (%s, %s),\n",
                joinColumnNames[0],
                inverseJoinColumnNames[0]
        ));

        query.append(String.format("FOREIGN KEY (%s) REFERENCES %s(%s),\n",
                joinColumnNames[0],
                SchemaGenerator.getTableName(entity),
                getPrimaryKeyColumnName(entity)
        ));

        query.append(String.format("FOREIGN KEY (%s) REFERENCES %s(%s)\n",
                inverseJoinColumnNames[0],
                SchemaGenerator.getTableName(field),
                getPrimaryKeyColumnName(field)
        ));

        query.append(");");

        return query.toString();
    }

    private static Class<?> getGenericType(Field field) {
        Type genericType = field.getGenericType();

        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();

            if (typeArguments.length > 0) {
                Type typeArgument = typeArguments[0];
                if (typeArgument instanceof Class<?>) {
                    return (Class<?>) typeArgument;
                }
            }
        }
        return null;
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

