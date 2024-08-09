package com.codex.framework.EntityManager.core;

import com.codex.framework.AnnotationScanner;
import com.codex.framework.DIContainer.Injector;
import com.codex.framework.DIContainer.utils.Utils;
import com.codex.framework.EntityManager.annotations.Entity.Entity;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;

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
