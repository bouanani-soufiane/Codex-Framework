package com.codex.framework.EntityManager.Core;

import com.codex.framework.AnnotationScanner;
import com.codex.framework.EntityManager.Annotations.Entity.Entity;
import com.codex.framework.EntityManager.Core.Shema.SchemaGenerator;

import java.sql.SQLException;
import java.util.Collection;

public class ProcessManager {
    private Class<?> application;
    private Collection<Class<?>> entities;
    private AnnotationScanner annotationScanner;
    private SchemaGenerator schemaGenerator;

    public ProcessManager( Class<?> application) throws SQLException {
        this.application = application;
        this.annotationScanner = new AnnotationScanner(Entity.class);
        this.entities = annotationScanner.find("com/codex");
        this.schemaGenerator = new SchemaGenerator(entities);

    }
    public void run() throws IllegalAccessException, NoSuchMethodException, SQLException {

       this.schemaGenerator.generateSchema();

    }
}
