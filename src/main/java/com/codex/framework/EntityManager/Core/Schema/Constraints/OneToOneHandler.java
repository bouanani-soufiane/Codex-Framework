package com.codex.framework.EntityManager.Core.Schema.Constraints;

import com.codex.framework.EntityManager.Annotations.Relationship.OneToOne;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import static com.codex.framework.EntityManager.Core.Schema.Resolver.*;

public class OneToOneHandler {

    /**
     * Collects SQL constraints for one-to-one relationships defined in the given entity class.
     *
     * @param entity The class representing the entity whose one-to-one constraints are to be collected.
     * @return A list of SQL queries to add foreign key constraints for the one-to-one relationships.
     */

    public static List<String> collectConstraints(Class<?> entity) {
        List<String> queries = new ArrayList<>();

        for (Field field : entity.getDeclaredFields()) {
            if (field.isAnnotationPresent(OneToOne.class)) {
                OneToOne oneToOne = field.getAnnotation(OneToOne.class);
                String tableName = getTableName(entity);
                String fieldName = resolveFieldName(field);
                String referencedTable = getTableName(field.getType());
                String referencedPrimaryKey = getPrimaryKeyColumnName(field.getType());
                String cascadeType = oneToOne.cascade();


                String query = String.format(
                        "ALTER TABLE %s \n" +
                                "\tADD COLUMN %s BIGINT, \n" +
                                "\tADD CONSTRAINT fk_%s FOREIGN KEY (%s) \n" +
                                "\tREFERENCES %s(%s) %s;\n",
                        tableName, fieldName, fieldName, fieldName, referencedTable, referencedPrimaryKey,
                        CascadeType.valueOf(cascadeType.toUpperCase()).toSql()
                );

                queries.add(query);
            }
        }

        return queries;
    }

}

