package com.codex.framework.EntityManager.Core.Schema;

import com.codex.framework.EntityManager.Annotations.Column.Column;
import com.codex.framework.EntityManager.Annotations.Entity.Table;
import com.codex.framework.EntityManager.Annotations.Id.ID;
import com.codex.framework.EntityManager.Annotations.Relationship.JoinColumn;
import com.codex.framework.EntityManager.Annotations.Relationship.OneToOne;
import com.codex.framework.EntityManager.Core.connection.DatabaseConnection;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

public class SchemaGenerator {

    private final Collection<Class<?>> entities;
    private final Connection conn;

    public SchemaGenerator(Collection<Class<?>> entities) throws SQLException {
        this.entities = entities;
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    public void generateSchema() throws SQLException {
        for (Class<?> entity : entities) {
            String tableName = getTableName(entity);
            String tableCreationQuery = buildCreateTableQuery(entity, tableName);
            executeTableCreation(tableName, tableCreationQuery);
        }
        addConstraints();
    }

    private String getTableName(Class<?> entity) {
        return entity.isAnnotationPresent(Table.class) ?
                entity.getAnnotation(Table.class).name() :
                entity.getSimpleName();
    }

    private String buildCreateTableQuery(Class<?> entity, String tableName) throws SQLException {
        StringBuilder query = new StringBuilder(String.format("CREATE TABLE IF NOT EXISTS %s (\n", tableName));

        for (Field field : entity.getDeclaredFields()) {
            if (field.isAnnotationPresent(ID.class)) {
                query.append("\t").append(buildPrimaryKeyColumn(field)).append(" INT PRIMARY KEY,\n");
            }
            if (field.isAnnotationPresent(Column.class)) {
                query.append(buildColumnDefinition(field));
            }
        }
        System.out.println("here : PK : "+ query);
        removeTrailingComma(query);
        query.append("\n);");

        return query.toString();
    }

    private String buildPrimaryKeyColumn(Field field) {
        ID idAnnotation = field.getAnnotation(ID.class);
        return (idAnnotation != null && !idAnnotation.name().isEmpty()) ? idAnnotation.name() : field.getName();

    }


    private String buildColumnDefinition(Field field) throws SQLException {
        Column column = field.getAnnotation(Column.class);

        String type = field.getType().isEnum() ? field.getType().getSimpleName() : Resolver.resolveType(field);
        if (column.length() > 0 || column.scale() > 0) {
            type = Resolver.resolveTypeWihtLenght(type, column.length(), column.scale());
        }

        if (field.getType().isEnum()) Resolver.CreateEnum(field.getType(), conn);

        return String.format("\t%s %s %s %s,\n",
                column.name().isEmpty() ? field.getName() : column.name(),
                type,
                column.nullable() ? "NULL" : "NOT NULL",
                column.unique() ? "UNIQUE" : ""
        );
    }

    private void removeTrailingComma(StringBuilder query) {
        int lastCommaIndex = query.lastIndexOf(",");
        if (lastCommaIndex != -1) {
            query.deleteCharAt(lastCommaIndex);
        }
    }

    private void executeTableCreation(String tableName, String query) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
            System.out.printf("Table %s created successfully.\n", tableName);
            System.out.println(query);
        } catch (SQLException e) {
            System.err.printf("Error creating table %s with query: %s\n%s\n", tableName, query, e.getMessage());
            throw e;
        }
    }

    public void addConstraints() throws SQLException {
        for (Class<?> entity : entities) {
            addOneToOneConstraints(entity);
        }
    }

    private void addOneToOneConstraints(Class<?> entity) throws SQLException {
        for (Field field : entity.getDeclaredFields()) {
            if (field.isAnnotationPresent(OneToOne.class)) {
                String tableName = getTableName(entity);
                String fieldName = resolveFieldName(field);
                String referencedTable = getTableName(field.getType());
                String referencedPrimaryKey = getPrimaryKeyColumnName(field.getType());

                String query = String.format(
                        "ALTER TABLE %s ADD COLUMN %s BIGINT, ADD CONSTRAINT fk_%s FOREIGN KEY (%s) REFERENCES %s(%s);",
                        tableName, fieldName, fieldName, fieldName, referencedTable, referencedPrimaryKey
                );

                executeQuery(query);
            }
        }
    }

    private String resolveFieldName(Field field) {
        if (field.isAnnotationPresent(JoinColumn.class)) {
            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            if (!joinColumn.name().isEmpty()) {
                return joinColumn.name();
            }
        }
        return field.getName();
    }

    private String getPrimaryKeyColumnName(Class<?> entityClass) {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(ID.class)) {
                ID idAnnotation = field.getAnnotation(ID.class);
                return (idAnnotation != null && !idAnnotation.name().isEmpty()) ? idAnnotation.name() : field.getName();
            }
        }
        throw new IllegalArgumentException("No @Id annotation found in " + entityClass.getSimpleName());
    }

    private void executeQuery(String query) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
            System.out.println("Executed query: " + query);
        } catch (SQLException e) {
            System.err.println("Error executing query: " + query + "\nError: " + e.getMessage());
            throw e;
        }
    }
}