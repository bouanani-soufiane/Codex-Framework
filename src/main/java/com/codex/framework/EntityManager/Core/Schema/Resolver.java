package com.codex.framework.EntityManager.Core.Schema;

import com.codex.framework.EntityManager.Annotations.Column.Column;
import com.codex.framework.EntityManager.Annotations.Entity.Table;
import com.codex.framework.EntityManager.Annotations.Id.ID;
import com.codex.framework.EntityManager.Annotations.Relationship.JoinColumn;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;

public abstract class Resolver {

    private static final Map<String, String> TYPE_MAP = TypeMapper.create().typeMap();

    /**
     * Resolves the SQL data type for a given field based on annotations and type mappings.
     * If a custom type is specified via the @Column annotation, it is used; otherwise, a default mapping is applied.
     *
     * @param field The field for which to resolve the data type.
     * @return The SQL data type as a string.
     */

    public static String resolveType(Field field) {
        Column columnAnnotation = field.getAnnotation(Column.class);
        if (columnAnnotation != null && !columnAnnotation.type().isEmpty()) {
            return columnAnnotation.type();
        }

        String type = TYPE_MAP.get(field.getType().getSimpleName());
        if (type != null) {
            return type;
        }

        if (isUnsupportedComplexType(field)) {
            throw new UnsupportedOperationException("Complex types like List, Set, and Map are not directly supported.");
        }

        return "VARCHAR";
    }

    /**
     * Checks if the field is of a complex type that is not directly supported (e.g., Collection or Map).
     *
     * @param field The field to check.
     * @return True if the field's type is a complex type; false otherwise.
     */

    public static boolean isUnsupportedComplexType(Field field) {
        return Collection.class.isAssignableFrom(field.getType()) || Map.class.isAssignableFrom(field.getType());
    }

    /**
     * Constructs a SQL representation of the enum values for a given enum type.
     *
     * @param enumType The enum type for which to generate SQL representation.
     * @return The SQL enum values as a string.
     */

    public static String resolveEnum(Class<?> enumType) {
        StringBuilder enumValues = new StringBuilder("(");

        for (Object enumConstant : enumType.getEnumConstants()) {
            enumValues.append("'").append(enumConstant.toString()).append("', ");
        }

        if (enumValues.length() > 1) {
            enumValues.setLength(enumValues.length() - 2);
        }
        enumValues.append(")");

        return enumValues.toString();
    }

    /**
     * Creates a PostgreSQL enum type based on the given enum class if it does not already exist.
     *
     * @param enumType The enum class representing the type to create.
     * @param conn The database connection used to execute the SQL query.
     * @throws SQLException If an error occurs while executing the SQL statement.
     */

    public static void CreateEnum(Class<?> enumType, Connection conn) throws SQLException {
        String typeName = enumType.getSimpleName().toLowerCase();
        String query = String.format(
                        "DO $$\n" +
                        "BEGIN\n" +
                        "    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = '%s') THEN\n" +
                        "        CREATE TYPE %s AS ENUM %s;\n" +
                        "    END IF;\n" +
                        "END $$;",
                typeName, enumType.getSimpleName(), resolveEnum(enumType)
        );

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
            System.out.println("Enum " + enumType.getSimpleName() + " created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating Enum " + enumType.getSimpleName()
                    + " with query: " + query
                    + "\nError: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves the table name associated with a given entity class based on the @Table annotation.
     *
     * @param entity The class representing the entity for which to resolve the table name.
     * @return The resolved table name, either from the @Table annotation or the entity class's simple name.
     */

    public static String getTableName(Class<?> entity) {
        Table tableAnnotation = entity.getAnnotation(Table.class);
        return (tableAnnotation != null && !tableAnnotation.name().isEmpty()) ? tableAnnotation.name() : entity.getSimpleName();
    }

    /**
     * Formats the SQL data type with length and scale based on the type provided.
     *
     * @param type The base SQL data type (e.g., VARCHAR, NUMERIC).
     * @param length The length of the field (for VARCHAR or CHAR).
     * @param scale The scale of the field (for NUMERIC).
     * @return The formatted SQL data type with length and scale.
     */

    public static String resolveTypeWihtLenght(String type, int length, int scale) {
        if ("VARCHAR".equals(type) || "CHAR".equals(type)) {
            return String.format("%s(%d)", type, length);
        } else if ("NUMERIC".equals(type)) {
            return String.format("%s(%d,%d)", type, length, scale);
        }
        return type;
    }

    /**
     * Retrieves the column name for the primary key of a given entity class.
     *
     * @param entityClass The class representing the entity.
     * @return The column name of the primary key.
     * @throws IllegalArgumentException If no @Id annotation is found in the class.
     */

    public static String getPrimaryKeyColumnName(Class<?> entityClass) {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(ID.class)) {
                ID idAnnotation = field.getAnnotation(ID.class);
                return (idAnnotation != null && !idAnnotation.name().isEmpty()) ? idAnnotation.name() : field.getName();
            }
        }
        throw new IllegalArgumentException("No @Id annotation found in " + entityClass.getSimpleName());
    }

    /**
     * Resolves the column name for a given field based on the @JoinColumn annotation or defaults to the field name.
     *
     * @param field The field for which to resolve the column name.
     * @return The resolved column name.
     */

    public static String resolveFieldName(Field field) {
        JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
        return (joinColumn != null && !joinColumn.name().isEmpty()) ? joinColumn.name() : field.getName();
    }

    /**
     * Extracts the generic type argument of a field if it is parameterized.
     *
     * @param field The field from which to extract the generic type.
     * @return The class of the generic type argument, or null if not applicable.
     */

    public static Class<?> getGenericType(Field field) {
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

    /**
     * Extracts the column names from an array of @JoinColumn annotations.
     *
     * @param joinColumns The array of @JoinColumn annotations.
     * @return An array of column names specified in the annotations.
     */

    public static String[] extractColumnNames(JoinColumn[] joinColumns) {
        String[] columnNames = new String[joinColumns.length];
        for (int i = 0; i < joinColumns.length; i++) {
            columnNames[i] = joinColumns[i].name();
        }
        return columnNames;
    }
}