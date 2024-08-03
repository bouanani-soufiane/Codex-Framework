package com.codex.framework;

import java.util.HashMap;
import java.util.Map;

public class Injector {

    private final Map<Class<?> , Class<?>> diMap = new HashMap<>();
    private final Map<Class<?> , Class<?>> appScope = new HashMap<>();


    Injector( AnnotationScanner annotationScanner , String packageName ){
    }




    private void initFramework ( Class<?> mainClass ) {
    }
}
