package com.codex.framework.EntityManager.Core.Schema.Constraints;

import com.codex.framework.EntityManager.Annotations.Relationship.OneToOne;
import com.codex.framework.EntityManager.Core.Schema.enums.CascadeType;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.codex.framework.EntityManager.Core.Schema.Resolver.*;

public class OneToOneHandler {

    /**
     * Collects SQL constraints for one-to-one relationships defined in the given entity class.
     *
     * @param entity The class representing the entity whose one-to-one constraints are to be collected.
     * @return A list of SQL queries to add foreign key constraints for the one-to-one relationships.
     */
    public static List<String> collectConstraints(Class<?> entity) {
        return Stream.of(entity.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(OneToOne.class))
                .map(field -> {
                    OneToOne oneToOne = field.getAnnotation(OneToOne.class);
                    String tableName = getTableName(entity);
                    String fieldName = resolveFieldName(field);
                    String referencedTable = getTableName(field.getType());
                    String referencedPrimaryKey = getPrimaryKeyColumnName(field.getType());
                    String cascadeType = oneToOne.cascade();

                    return String.format(
                            """
                            ALTER TABLE %s
                            \tADD COLUMN %s BIGINT,
                            \tADD CONSTRAINT fk_%s FOREIGN KEY (%s)
                            \tREFERENCES %s(%s) %s;
                            """,
                            tableName, fieldName, fieldName, fieldName, referencedTable, referencedPrimaryKey,
                            CascadeType.valueOf(cascadeType.toUpperCase()).toSql()
                    );
                })
                .collect(Collectors.toList());
    }
}
