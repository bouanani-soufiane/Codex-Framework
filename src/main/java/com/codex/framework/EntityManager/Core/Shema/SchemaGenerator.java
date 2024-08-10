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
import java.util.Objects;

public class SchemaGenerator {

    private Collection<Class<?>> entities;
    private Connection conn;
    public SchemaGenerator(Collection<Class<?>> entities) throws SQLException {
        this.entities = entities;
        this.conn = DatabaseConnection.getInstance().getConnection();

    }

    public void generateSchema() throws SQLException {
        for (Class<?> entity : entities) {
            String table = entity.isAnnotationPresent(Table.class) ? entity.getAnnotation(Table.class).name() : entity.getSimpleName();
            StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS " + table + "(\n");
            for (Field field : entity.getDeclaredFields()) {
                if (field.isAnnotationPresent(ID.class)) {
                    query.append("\t").append(field.getName()).append( " INT PRIMARY KEY , ");
                }
                if (field.isAnnotationPresent(Column.class)) {
                    if(field.getType().isEnum()){
                        Resolver.getOrCreateEnum(field.getType(),conn);
                    }
                    String name = field.getAnnotation(Column.class).name().isEmpty() ? field.getName() : field.getAnnotation(Column.class).name()  ;
                    String type =  field.getType().isEnum() ? field.getType().getSimpleName() : Resolver.resolveType(field);
                    String nullable = field.getAnnotation(Column.class).nullable() ? " NULL" : " NOT NULL";
                    String unique = field.getAnnotation(Column.class).unique() ? " UNIQUE" : "";
                    query.append("\n \t").append(name + " ").append(type).append(nullable).append(unique).append(", ");
                }
            }
            query.setLength(query.length()-2);
            query.append("\n);");


            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(query.toString());
                System.out.println("Table " + table + " created successfully.");
                System.out.println(query);

            } catch (SQLException e) {
                System.err.println("Error creating table " + table + " with : " + query + " \n" + e.getMessage());
            }
        }
    }



    public void addConstraints(){

    }
}
