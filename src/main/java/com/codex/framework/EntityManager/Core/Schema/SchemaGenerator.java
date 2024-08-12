
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

import static com.codex.framework.EntityManager.Core.Schema.Resolver.getTableName;

public class SchemaGenerator {

    private final Collection<Class<?>> entities;
    private final Connection conn;

    public SchemaGenerator(Collection<Class<?>> entities) throws SQLException {
        this.entities = entities;
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Generates the schema for all the provided entities by creating tables and adding constraints.
     *
     * @throws SQLException If a database access error occurs.
     */

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

    /**
     * Builds a SQL query for creating a table based on the entity class and table name.
     *
     * @param entity The class representing the entity.
     * @param tableName The name of the table to be created.
     * @return A SQL query string for creating the table.
     * @throws SQLException If an error occurs while processing the field annotations.
     */

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

    /**
     * Builds the SQL definition for a primary key column based on the field's @ID annotation.
     *
     * @param field The field representing the primary key.
     * @return A SQL string defining the primary key column.
     */

    private String buildPrimaryKeyColumn(Field field) {
        ID idAnnotation = field.getAnnotation(ID.class);
        String columnName = (idAnnotation != null && !idAnnotation.name().isEmpty()) ? idAnnotation.name() : field.getName();
        return String.format("\t%s INT PRIMARY KEY,\n", columnName);
    }

    /**
     * Builds the SQL definition for a column based on the field's @Column annotation.
     *
     * @param field The field representing the column.
     * @return A SQL string defining the column.
     * @throws SQLException If an error occurs while processing the column annotations.
     */

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

    /**
     * Removes the trailing comma from a SQL query string.
     *
     * @param query The StringBuilder object containing the SQL query.
     */

    private void removeTrailingComma(StringBuilder query) {
        int lastCommaIndex = query.lastIndexOf(",");
        if (lastCommaIndex != -1) {
            query.deleteCharAt(lastCommaIndex);
        }
    }

    /**
     * Executes a batch of SQL queries.
     *
     * @param queries A list of SQL query strings to be executed.
     * @throws SQLException If an error occurs while executing the queries.
     */

    private void executeBatch(List<String> queries) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            for (String query : queries) {
                stmt.addBatch(query);
            }
            stmt.executeBatch();
            System.out.println("Batch executed successfully.\n");
            for (String query : queries) {
                System.out.println(query + "\n");
            }
        } catch (SQLException e) {
            System.err.println("Error executing batch: " + e.getMessage());
            throw e;
        }
    }
}
