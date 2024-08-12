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
        for(Class<?> entity : entities){
            oneToOne(entity);
        }
    }

    public void oneToOne(Class<?> entity) throws SQLException {
        String query = "";

        for (Field field : entity.getDeclaredFields()) {
            if (field.isAnnotationPresent(OneToOne.class)) {

                String tableName = getTableName(entity);
                String fieldName = field.isAnnotationPresent(JoinColumn.class) && !field.getAnnotation(JoinColumn.class).name().isEmpty() ? field.getAnnotation(JoinColumn.class).name() : field.getName();
                String reference = field.getType().getSimpleName().toLowerCase();
                String referencePK = getPrimaryKeyColumnName(field.getType());                //String referencePK = "em_id";
                query = "ALTER TABLE "+ tableName+ "\n" + "ADD COLUMN " + fieldName+ " BIGINT,\n"
                + "ADD CONSTRAINT " + "fk_" + fieldName + " FOREIGN KEY (" + fieldName + ")" + " REFERENCES " + reference + "("+referencePK+")" + ";";


            }
        }

        try(Statement stmt = conn.createStatement()){
            stmt.executeUpdate(query);
        }catch (Exception e){
            throw new SQLException(e);
        }

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


}
