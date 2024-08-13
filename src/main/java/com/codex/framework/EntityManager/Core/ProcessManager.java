package com.codex.framework.EntityManager.Core;

import com.codex.framework.AnnotationScanner;
import com.codex.framework.DIContainer.Injector;
import com.codex.framework.EntityManager.Annotations.Entity.Entity;
import com.codex.framework.EntityManager.Core.Exceptions.SchemaGenerationException;
import com.codex.framework.EntityManager.Core.Exceptions.InitializationException;
import com.codex.framework.EntityManager.Core.Schema.SchemaGenerator;

import java.sql.SQLException;
import java.util.Collection;

public class ProcessManager {
    private Class<?> application;
    private Collection<Class<?>> entities;
    private AnnotationScanner annotationScanner;
    private SchemaGenerator schemaGenerator;
    private Injector injector;

    public ProcessManager(Class<?> application) throws SchemaGenerationException, SQLException {
        this.application = application;
        try {
            this.annotationScanner = new AnnotationScanner(Entity.class);
            this.entities = annotationScanner.find(application.getPackageName().replace(".", "/"));
            this.schemaGenerator = new SchemaGenerator(entities);
            this.injector = Injector.getInstance();
        } catch (Exception e) {
            throw new InitializationException("Error initializing ProcessManager components", e);
        }
    }

    private void initFramework() throws InitializationException {
        try {
            injector.initFramework(application);
        } catch (Exception e) {
            throw new InitializationException("Error initializing framework with injector", e);
        }
    }

    public void run() throws InitializationException, SchemaGenerationException {
        try {
            initFramework();
            this.schemaGenerator.generateSchema();
        } catch (Exception e) {
            throw new InitializationException("Error running ProcessManager", e);
        }
    }
}
