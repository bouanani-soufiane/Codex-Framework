package com.codex.framework.EntityManager.Core.Schema;

import com.codex.framework.EntityManager.Annotations.Column.Column;
import com.codex.framework.EntityManager.Annotations.Id.ID;
import com.codex.framework.EntityManager.Core.Exceptions.SchemaGenerationException;
import com.codex.framework.EntityManager.Core.Exceptions.QueryExecutionException;
import com.codex.framework.EntityManager.Core.Schema.Constraints.ManyToManyHandler;
import com.codex.framework.EntityManager.Core.Schema.Constraints.ManyToOneHandler;
import com.codex.framework.EntityManager.Core.Schema.Constraints.OneToOneHandler;
import com.codex.framework.EntityManager.Core.connection.DatabaseConnection;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static com.codex.framework.EntityManager.Core.Schema.Resolver.getTableName;

public class SchemaGenerator {

    private final Collection<Class<?>> entities;
    private final Connection conn;

    public SchemaGenerator(Collection<Class<?>> entities) throws SchemaGenerationException {
        this.entities = entities;
        try {
            this.conn = DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            throw new SchemaGenerationException("Error getting database connection", e);
        }
    }

    /**
     * Retrieves all constraints for a given table in the schema.
     *
     * @param tableName The name of the table.
     * @return A list of constraint names for the given table.
     * @throws SchemaGenerationException If a database access error occurs.
     */

    private List<String> getTableConstraints(String tableName) throws SchemaGenerationException {
        List<String> constraints = new ArrayList<>();

        String schemaName = "public";

        String query = String.format(
                "SELECT con.conname " +
                        "FROM pg_catalog.pg_constraint con " +
                        "INNER JOIN pg_catalog.pg_class rel ON rel.oid = con.conrelid " +
                        "INNER JOIN pg_catalog.pg_namespace nsp ON nsp.oid = connamespace " +
                        "WHERE nsp.nspname = '%s' AND rel.relname = '%s';",
                schemaName, tableName
        );

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                constraints.add(rs.getString("conname"));
            }
        } catch (SQLException e) {
            throw new SchemaGenerationException("Error retrieving table constraints", e);
        }

        return constraints;
    }

    /**
     * Drops all constraints and tables before generating the new schema.
     *
     * @throws SchemaGenerationException If a database access error occurs.
     */

    private void dropConstraintsAndTables() throws SchemaGenerationException {
        List<String> dropConstraintQueries = new ArrayList<>();
        List<String> dropTableQueries = new ArrayList<>();

        for (Class<?> entity : entities) {
            String tableName = getTableName(entity);
            List<String> constraints = getTableConstraints(tableName);

            for (String constraint : constraints) {
                dropConstraintQueries.add(String.format("ALTER TABLE %s DROP CONSTRAINT %s;", tableName, constraint));
            }
            dropTableQueries.add(String.format("DROP TABLE IF EXISTS %s CASCADE;", tableName));
        }

        try {
            executeBatch(dropConstraintQueries);
            executeBatch(dropTableQueries);

        } catch (QueryExecutionException e) {
            throw new SchemaGenerationException("Error dropping constraints and tables", e);
        }
    }

    /**
     * Generates the schema for all the provided entities by creating tables and adding constraints.
     *
     * @throws SchemaGenerationException If a database access error occurs.
     */

    public void generateSchema() throws SchemaGenerationException {
        dropConstraintsAndTables();
        List<String> tableCreationQueries = new ArrayList<>();
        List<String> constraintQueries = new ArrayList<>();

        for (Class<?> entity : entities) {
            String tableName = getTableName(entity);
            String tableCreationQuery;
            try {
                tableCreationQuery = buildCreateTableQuery(entity, tableName);
            } catch (SQLException e) {
                throw new SchemaGenerationException("Error building create table query for " + tableName, e);
            }
            tableCreationQueries.add(tableCreationQuery);

            constraintQueries.addAll(OneToOneHandler.collectConstraints(entity));
            constraintQueries.addAll(ManyToOneHandler.collectConstraints(entity));
            constraintQueries.addAll(ManyToManyHandler.collectConstraints(entity));
        }

        try {
            executeBatch(tableCreationQueries);
            executeBatch(constraintQueries);
        } catch (QueryExecutionException e) {
            throw new SchemaGenerationException("Error generating schema", e);
        }
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
     * @throws QueryExecutionException If an error occurs while executing the queries.
     */

    private void executeBatch(List<String> queries) throws QueryExecutionException {
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
            throw new QueryExecutionException("Error executing batch queries", e);
        }
    }
}
