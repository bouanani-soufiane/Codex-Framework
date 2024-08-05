package com.codex.framework;

import com.codex.framework.annotations.Autowired;
import com.codex.framework.annotations.Component;
import com.codex.framework.annotations.Qualifier;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

public class Injector {

    private final Utils utils;
    public Injector(){
        this.utils = new Utils();
    }

    AnnotationScanner annotationScanner = new AnnotationScanner(Component.class);

    private final Map<Class<?> , List<Class<?>>> interfaceImplementationsMap = new HashMap<>();
    private final Map<Class<?> , Object> applicationInstanceCache = new HashMap<>();

    Collection<Class<?>> componentClasses = annotationScanner.find("com/codex");

    public void initFramework ( Class<?> mainClass ) throws IllegalAccessException {

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
        for (Class<?> clazz : componentClasses) {
            this.autowiredField(clazz);
            this.autowiredConstructor(clazz);
        }

    }


    private void autowiredConstructor (Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            if(constructor.isAnnotationPresent(Autowired.class)){
                constructor.setAccessible(true);
                System.out.println("const : " + constructor);

            }
        }

    }




    private void autowiredField(Class<?> clazz) throws IllegalAccessException {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Qualifier.class)) {
                this.injectField(clazz, field, field.getAnnotation(Qualifier.class).value());
            } else if (field.isAnnotationPresent(Autowired.class)) {
                Class<?> fieldType = field.getType();

                if (!fieldType.isInterface() && applicationInstanceCache.containsKey(fieldType)) {
                    this.injectField(clazz, field, fieldType);
                } else if (!interfaceImplementationsMap.containsKey(fieldType)) {
                    throw new RuntimeException("Field '" + field.getName() + "' of type '" + fieldType.getName() +
                            "' is annotated with @Autowired but no implementation is registered. Ensure a component is annotated with @Component.");
                } else {
                    List<Class<?>> implementations = interfaceImplementationsMap.get(fieldType);
                    if (implementations == null || implementations.isEmpty()) {
                        throw new RuntimeException("No implementations found for interface '" + fieldType.getName() +
                                "'. Ensure at least one class implements the interface and is annotated with @Component.");
                    } else if (implementations.size() > 1) {
                        throw new RuntimeException("Multiple implementations found for interface '" + fieldType.getName() +
                                "'. Use @Qualifier to specify the desired implementation.");
                    } else {
                        this.injectField(clazz, field, implementations.get(0));
                    }
                }
            }
        }
    }



    public Object getBean(Class<?> clazz) {
        return applicationInstanceCache.get(clazz);
    }



    private void injectField(Class<?> clazz, Field field, Class<?> implementation) throws IllegalAccessException {
        field.setAccessible(true);

        Object instance = applicationInstanceCache.get(clazz);
        if (instance == null) {
            throw new RuntimeException("No instance found for" + clazz.getName() + " in cache.");
        }

        Object value = applicationInstanceCache.get(implementation);
        if (value == null) {
            throw new RuntimeException("No instance found for " + implementation);
        }
        field.set(instance, value);
    }


    private void registerImplementation (Class<?> i , Class<?> impl ){
        if (!interfaceImplementationsMap.containsKey(i)){
            interfaceImplementationsMap.put(i, new ArrayList<>( Collections.singletonList(impl)));
        }else{
            interfaceImplementationsMap.get(i).add(impl);
        }
    }




}