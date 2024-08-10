package com.codex.framework.EntityManager.Core.Shema;

import com.codex.framework.EntityManager.Annotations.Column.Column;
import com.codex.framework.EntityManager.Annotations.Entity.Table;
import com.codex.framework.EntityManager.Annotations.Id.ID;
import com.codex.framework.EntityManager.Core.connection.DatabaseConnection;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

public class SchemaGenerator {

    private Collection<Class<?>> entities;
    private Connection conn;
    public SchemaGenerator(Collection<Class<?>> entities) throws SQLException {
        this.entities = entities;
        this.conn = DatabaseConnection.getInstance().getConnection();

    }

    public void generateSchema(){
        for (Class<?> entity : entities) {
            String table = entity.isAnnotationPresent(Table.class) ? entity.getAnnotation(Table.class).name() : entity.getSimpleName();
            StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS " + table);
            for (Field field : entity.getDeclaredFields()) {
                if (field.isAnnotationPresent(ID.class)) {
                    query.append("( ").append(field.getName()).append( " INT PRIMARY KEY ,");
                }
                if (field.isAnnotationPresent(Column.class)) {
                    String name = field.isAnnotationPresent(Column.class) ? field.getAnnotation(Column.class).name() : field.getName();
                    query.append(" ").append(name + " ").append(field.getAnnotation(Column.class).type());
                }
            }
            query.append(" );");

            System.out.println(query.toString());

            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(query.toString());
                System.out.println("Table " + table + " created successfully.");
            } catch (SQLException e) {
                System.err.println("Error creating table " + table + ": " + e.getMessage());
            }
        }
    }
    public void addConstraints(){

    }
}
