package com.codex.framework.EntityManager.Core.Schema.Constraints;

import com.codex.framework.EntityManager.Annotations.Relationship.ManyToOne;

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
            if (field.isAnnotationPresent(ManyToOne.class)) {
                ManyToOne manyOne = field.getAnnotation(ManyToOne.class);
                String tableName = getTableName(entity);
                String fieldName = resolveFieldName(field);
                String referencedTable = getTableName(field.getType());
                String referencedPrimaryKey = getPrimaryKeyColumnName(field.getType());
                String cascadeType = manyOne.cascade();

                String query = String.format(
                        """
                                ALTER TABLE %s\s
                                \tADD COLUMN %s BIGINT,\s
                                \tADD CONSTRAINT fk_%s FOREIGN KEY (%s)\s
                                \tREFERENCES %s(%s) %s;
                                """,

                        tableName, fieldName, fieldName, fieldName, referencedTable, referencedPrimaryKey,
                        CascadeType.valueOf(cascadeType.toUpperCase()).toSql()
                );

                queries.add(query);
            }
        }

        return queries;
    }


}
