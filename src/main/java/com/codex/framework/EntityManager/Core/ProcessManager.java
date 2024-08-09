package com.codex.framework.EntityManager.Core;

import com.codex.framework.AnnotationScanner;
import com.codex.framework.EntityManager.Annotations.Entity.Entity;

import java.util.Collection;

public class ProcessManager {
    private Class<?> application;
    private Collection<Class<?>> entities;
    private AnnotationScanner annotationScanner;

    public ProcessManager( Class<?> application) {
        this.application = application;
        this.annotationScanner = new AnnotationScanner(Entity.class);
        this.entities = annotationScanner.find("com/codex");


    }
    public void run() throws IllegalAccessException, NoSuchMethodException {

       System.out.println("here : " + entities);

    }
}
