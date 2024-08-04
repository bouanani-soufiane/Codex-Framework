package com.codex.framework;

import com.codex.framework.annotations.Component;
import com.codex.testing.UserAccountClientComponent;

import java.util.*;

public class Injector {

    public Injector(){}

    AnnotationScanner annotationScanner = new AnnotationScanner(Component.class);

    private final Map<Class<?> , List<Class<?>>> interfaceImplementationsMap = new HashMap<>();
    private final Map<Class<?> , Object> applicationInstanceCache = new HashMap<>();

    Collection<Class<?>> componentClasses = annotationScanner.find("com/codex");

    public void initFramework ( Class<?> mainClass ) {

        for (Class<?> clazz : componentClasses){
                Class<?>[] interfaces = clazz.getInterfaces();

                if (interfaces.length == 0){
                    interfaceImplementationsMap.put(clazz, Collections.singletonList(clazz));
                }else{
                    for (Class<?> i : interfaces){
                        registerImplementation(i , clazz);                    }
                }
            try{
               Object instance = clazz.getDeclaredConstructor().newInstance();
               applicationInstanceCache.put(clazz , instance);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    private void registerImplementation (Class<?> i , Class<?> impl ){
        if (!interfaceImplementationsMap.containsKey(i)){
            interfaceImplementationsMap.put(i, new ArrayList<>( Collections.singletonList(impl)));
        }else{
            interfaceImplementationsMap.get(i).add(impl);
        }
    }


    Injector( AnnotationScanner annotationScanner , String packageName ){
    }

    private void printHashMap(Map<?, ?> map) {
        if (map == null || map.isEmpty()) {
            System.out.println("The map is empty or null.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("HashMap Contents:\n");
        sb.append("-----------------\n");

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            sb.append("Key: ").append(entry.getKey())
                    .append(", Value: ").append(entry.getValue())
                    .append("\n");
        }

        System.out.println(sb.toString());
    }

}
