package com.codex.framework.EntityManager.Core.Schema.Constraints;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import static com.codex.framework.EntityManager.Core.Schema.Resolver.*;

public class ManyToOneHandler {

    /**
     * Collects SQL constraints for many-to-one relationships defined in the given entity class.
     *
     * @param entity The class representing the entity whose many-to-one constraints are to be collected.
     * @return A list of SQL queries to add foreign key constraints for the many-to-one relationships.
     */

    public static List<String> collectConstraints(Class<?> entity) {
        List<String> queries = new ArrayList<>();

        for (Field field : entity.getDeclaredFields()) {
            if (field.isAnnotationPresent(com.codex.framework.EntityManager.Annotations.Relationship.ManyToOne.class)) {
                String tableName = getTableName(entity);
                String fieldName = resolveFieldName(field);
                String referencedTable = getTableName(field.getType());
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


}
