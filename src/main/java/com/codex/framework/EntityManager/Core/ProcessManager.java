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
        this.entities = annotationScanner.find(application.getPackageName().replace("." ,"/"));
        this.schemaGenerator = new SchemaGenerator(entities);
        this.injector = Injector.getInstance();
    }

    private void initFramework() throws IllegalAccessException, NoSuchMethodException {
        injector.initFramework(application);
    }

    public void run() throws SQLException, IllegalAccessException, NoSuchMethodException {
        initFramework();
        this.schemaGenerator.generateSchema();
    }
}
