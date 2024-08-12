package com.codex.framework.EntityManager.Core.Schema.Constraints;

import com.codex.framework.EntityManager.Annotations.Relationship.JoinTable;
import com.codex.framework.EntityManager.Annotations.Relationship.ManyToMany;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import static com.codex.framework.EntityManager.Core.Schema.Resolver.*;

public class ManyToManyHandler {

    /**
     * Collects the constraints for many-to-many relationships in the provided entity class.
     *
     * @param entity The class representing the entity.
     * @return A list of SQL query strings for creating many-to-many relationship constraints.
     */

    public static List<String> collectConstraints(Class<?> entity) {
        List<String> queries = new ArrayList<>();

        for (Field field : entity.getDeclaredFields()) {
            if (field.isAnnotationPresent(ManyToMany.class)) {
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

    /**
     * Builds a SQL query for creating a join table based on the many-to-many relationship.
     *
     * @param joinTableName The name of the join table.
     * @param joinColumnNames The names of the columns representing the entity's side of the relationship.
     * @param inverseJoinColumnNames The names of the columns representing the related entity's side of the relationship.
     * @param entity The class representing the entity on the "owning" side of the relationship.
     * @param field The class representing the related entity on the "inverse" side of the relationship.
     * @return A SQL query string for creating the join table with the appropriate foreign key constraints.
     */

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
        query.append(String.format("\tFOREIGN KEY (%s) REFERENCES %s(%s),\n",
                joinColumnNames[0],
                getTableName(entity),
                getPrimaryKeyColumnName(entity)
        ));
        query.append(String.format("\tFOREIGN KEY (%s) REFERENCES %s(%s)\n",
                inverseJoinColumnNames[0],
                getTableName(field),
                getPrimaryKeyColumnName(field)
        ));
        query.append(");");

        return query.toString();
    }


}

