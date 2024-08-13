package com.codex.framework.EntityManager.Core;

import com.codex.framework.AnnotationScanner;
import com.codex.framework.DIContainer.Injector;
import com.codex.framework.EntityManager.Annotations.Entity.Entity;
import com.codex.framework.EntityManager.Core.Schema.SchemaGenerator;

import java.sql.SQLException;
import java.util.Collection;

public class ProcessManager {
    private Class<?> application;
    private Collection<Class<?>> entities;
    private AnnotationScanner annotationScanner;
    private SchemaGenerator schemaGenerator;
    private Injector injector;

    public ProcessManager(Class<?> application) throws SQLException {
        this.application = application;
        this.annotationScanner = new AnnotationScanner(Entity.class);
        this.entities = annotationScanner.find("com/codex");
        this.schemaGenerator = new SchemaGenerator(entities);
        this.injector = new Injector();  // Initialize the Injector
    }

    /** Initializes the framework by binding interfaces to their implementations and injecting dependencies into components. */
    private void initFramework() throws IllegalAccessException, NoSuchMethodException {
        injector.initFramework(application);
    }

    public void run() throws SQLException, IllegalAccessException, NoSuchMethodException {
        initFramework(); // Initialize the Injector
        this.schemaGenerator.generateSchema();
    }

    /** Provides access to the Injector instance. */
    public Injector getInjector() {
        return injector;
    }
}
