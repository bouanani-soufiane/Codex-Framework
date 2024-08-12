
package com.codex.framework.EntityManager.Core.Schema;

import com.codex.framework.EntityManager.Annotations.Column.Column;
import com.codex.framework.EntityManager.Annotations.Entity.Table;
import com.codex.framework.EntityManager.Annotations.Id.ID;
import com.codex.framework.EntityManager.Core.Schema.Constraints.ManyToManyHandler;
import com.codex.framework.EntityManager.Core.Schema.Constraints.ManyToOneHandler;
import com.codex.framework.EntityManager.Core.Schema.Constraints.OneToOneHandler;
import com.codex.framework.EntityManager.Core.connection.DatabaseConnection;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class SchemaGenerator {

    private final Collection<Class<?>> entities;
    private final Connection conn;

    public SchemaGenerator(Collection<Class<?>> entities) throws SQLException {
        this.entities = entities;
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    public void generateSchema() throws SQLException {
        List<String> tableCreationQueries = new ArrayList<>();
        List<String> constraintQueries = new ArrayList<>();

        for (Class<?> entity : entities) {
            String tableName = getTableName(entity);
            String tableCreationQuery = buildCreateTableQuery(entity, tableName);
            tableCreationQueries.add(tableCreationQuery);

            constraintQueries.addAll(OneToOneHandler.collectConstraints(entity));
            constraintQueries.addAll(ManyToOneHandler.collectConstraints(entity));
            constraintQueries.addAll(ManyToManyHandler.collectConstraints(entity));

        }

        executeBatch(tableCreationQueries);
        executeBatch(constraintQueries);
    }

    public static String getTableName(Class<?> entity) {
        Table tableAnnotation = entity.getAnnotation(Table.class);
        return (tableAnnotation != null && !tableAnnotation.name().isEmpty()) ? tableAnnotation.name() : entity.getSimpleName();
    }

    private String buildCreateTableQuery(Class<?> entity, String tableName) throws SQLException {
        StringBuilder query = new StringBuilder(String.format("CREATE TABLE IF NOT EXISTS %s (\n", tableName));

        for (Field field : entity.getDeclaredFields()) {
            if (field.isAnnotationPresent(ID.class)) {
                query.append(buildPrimaryKeyColumn(field));
            }
            if (field.isAnnotationPresent(Column.class)) {
                query.append(buildColumnDefinition(field));
            }
        }

        removeTrailingComma(query);
        query.append("\n);");

        return query.toString();
    }

    private String buildPrimaryKeyColumn(Field field) {
        ID idAnnotation = field.getAnnotation(ID.class);
        String columnName = (idAnnotation != null && !idAnnotation.name().isEmpty()) ? idAnnotation.name() : field.getName();
        return String.format("\t%s INT PRIMARY KEY,\n", columnName);
    }

    private String buildColumnDefinition(Field field) throws SQLException {
        Column column = field.getAnnotation(Column.class);

        String type = field.getType().isEnum() ? field.getType().getSimpleName() : Resolver.resolveType(field);
        if (column.length() > 0 || column.scale() > 0) {
            type = Resolver.resolveTypeWihtLenght(type, column.length(), column.scale());
        }

        if (field.getType().isEnum()) {
            Resolver.CreateEnum(field.getType(), conn);
        }

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

    private void executeBatch(List<String> queries) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            for (String query : queries) {
                stmt.addBatch(query);
            }
            stmt.executeBatch();
            System.out.println("Batch executed successfully.");
        } catch (SQLException e) {
            System.err.println("Error executing batch: " + e.getMessage());
            throw e;
        }
    }
}
