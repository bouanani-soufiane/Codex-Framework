package com.codex;

import com.codex.framework.AnnotationScanner;
import com.codex.framework.annotations.Component;

import java.util.Collection;

public class Main {
    public static void main(String[] args) {
        AnnotationScanner annotationScanner = new AnnotationScanner(Component.class);
        Collection<Class<?>> classes = annotationScanner.find("com/codex");

        for (Class<?> cls : classes) {
            System.out.println("Found class: " + cls.getName());
        }
    }

}

