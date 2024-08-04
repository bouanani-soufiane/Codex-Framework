package com.codex.framework;

import com.codex.framework.annotations.Autowired;
import com.codex.framework.annotations.Component;
import com.codex.framework.annotations.Qualifier;
import com.codex.testing.services.UserService;

import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import java.lang.reflect.Field;
import java.util.*;

public class Injector {

    private Utils utils;
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
        }

    }

    private void autowiredField(Class<?> clazz) throws IllegalAccessException {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if(field.isAnnotationPresent(Qualifier.class)){
                System.out.println("here : " + field.getAnnotation(Qualifier.class).value());
                this.injectField(clazz , field , field.getAnnotation(Qualifier.class).value());

            } else if (field.isAnnotationPresent(Autowired.class) ) {
                Class<?> fieldType = field.getType();
                if (!interfaceImplementationsMap.containsKey(fieldType)) {
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
                        this.injectField(clazz, field, implementations.getFirst());
                        System.out.println("Injected field '" + field.getName() + "' in class '" + clazz.getName() + "'");
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
            throw new RuntimeException("Instance for class '" + clazz.getName() + "' not found in cache.");
        }

        Object value = applicationInstanceCache.get(implementation);
        if (value == null) {
            throw new RuntimeException("Instance for implementation '" + implementation.getName() + "' not found in cache.");
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
