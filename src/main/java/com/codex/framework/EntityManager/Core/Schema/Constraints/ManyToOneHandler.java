package com.codex.framework.EntityManager.Core.Schema.Constraints;

import com.codex.framework.EntityManager.Annotations.Relationship.ManyToOne;
import com.codex.framework.EntityManager.Core.Schema.enums.CascadeType;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.codex.framework.EntityManager.Core.Schema.Resolver.*;

public class ManyToOneHandler {

    /**
     * Collects SQL constraints for many-to-one relationships defined in the given entity class.
     *
     * @param entity The class representing the entity whose many-to-one constraints are to be collected.
     * @return A list of SQL queries to add foreign key constraints for the many-to-one relationships.
     */
    public static List<String> collectConstraints(Class<?> entity) {
        return Stream.of(entity.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ManyToOne.class))
                .map(field -> {
                    ManyToOne manyOne = field.getAnnotation(ManyToOne.class);
                    String tableName = getTableName(entity);
                    String fieldName = resolveFieldName(field);
                    String referencedTable = getTableName(field.getType());
                    String referencedPrimaryKey = getPrimaryKeyColumnName(field.getType());
                    String cascadeType = manyOne.cascade();

                    return String.format(
                            """
                                    ALTER TABLE %s\s
                                    \tADD COLUMN %s BIGINT,\s
                                    \tADD CONSTRAINT fk_%s FOREIGN KEY (%s)\s
                                    \tREFERENCES %s(%s) %s;
                                    """,

                            tableName, fieldName, fieldName, fieldName, referencedTable, referencedPrimaryKey,
                            CascadeType.valueOf(cascadeType.toUpperCase()).toSql()
                    );
                })
                .collect(Collectors.toList());
    }
}
