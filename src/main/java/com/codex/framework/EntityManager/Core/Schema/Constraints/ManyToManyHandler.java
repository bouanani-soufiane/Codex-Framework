package com.codex.framework.EntityManager.Core.Schema.Constraints;

import com.codex.framework.EntityManager.Annotations.Relationship.JoinTable;
import com.codex.framework.EntityManager.Annotations.Relationship.ManyToMany;
import com.codex.framework.EntityManager.Core.Schema.enums.CascadeType;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static com.codex.framework.EntityManager.Core.Schema.Resolver.*;

public class ManyToManyHandler {

    /**
     * Collects the constraints for many-to-many relationships in the provided entity class.
     *
     * @param entity The class representing the entity.
     * @return A list of SQL query strings for creating many-to-many relationship constraints.
     */
    public static List<String> collectConstraints(Class<?> entity) {
        return Stream.of(entity.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ManyToMany.class))
                .map(field -> {
                    JoinTable joinTable = field.getAnnotation(JoinTable.class);
                    if (joinTable != null) {
                        String joinTableName = joinTable.name();
                        String[] joinColumnNames = extractColumnNames(joinTable.joinColumns());
                        String[] inverseJoinColumnNames = extractColumnNames(joinTable.inverseJoinColumns());
                        Class<?> fieldClass = getGenericType(field);
                        return buildJoinTableQuery(joinTableName, joinColumnNames, inverseJoinColumnNames, entity, fieldClass, field);
                    }
                    return null;
                })
                .filter(query -> query != null)
                .collect(Collectors.toList());
    }

    /**
     * Builds a SQL query for creating a join table based on the many-to-many relationship.
     *
     * @param joinTableName The name of the join table.
     * @param joinColumnNames The names of the columns representing the entity's side of the relationship.
     * @param inverseJoinColumnNames The names of the columns representing the related entity's side of the relationship.
     * @param entity The class representing the entity on the "owning" side of the relationship.
     * @param fieldClass The class representing the related entity on the "inverse" side of the relationship.
     * @param field The field representing the many-to-many relationship.
     * @return A SQL query string for creating the join table with the appropriate foreign key constraints.
     */
    private static String buildJoinTableQuery(String joinTableName, String[] joinColumnNames, String[] inverseJoinColumnNames,
                                              Class<?> entity, Class<?> fieldClass, Field field) {
        ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
        String cascadeType = manyToMany.cascade();

        StringBuilder query = new StringBuilder(String.format("CREATE TABLE IF NOT EXISTS %s (\n", joinTableName));

        Stream.of(joinColumnNames)
                .forEach(columnName -> query.append(String.format("\t%s BIGINT,\n", columnName)));

        Stream.of(inverseJoinColumnNames)
                .forEach(columnName -> query.append(String.format("\t%s BIGINT,\n", columnName)));

        query.append(String.format("\tPRIMARY KEY (%s, %s),\n", joinColumnNames[0], inverseJoinColumnNames[0]));

        query.append(String.format("\tFOREIGN KEY (%s) REFERENCES %s(%s) %s,\n",
                joinColumnNames[0],
                getTableName(entity),
                getPrimaryKeyColumnName(entity),
                CascadeType.valueOf(cascadeType.toUpperCase()).toSql()
        ));

        query.append(String.format("\tFOREIGN KEY (%s) REFERENCES %s(%s) %s\n",
                inverseJoinColumnNames[0],
                getTableName(fieldClass),
                getPrimaryKeyColumnName(fieldClass),
                CascadeType.valueOf(cascadeType.toUpperCase()).toSql()
        ));

        query.append(");");

        return query.toString();
    }

}
