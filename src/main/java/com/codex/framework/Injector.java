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

    public void initFramework(Class<?> mainClass) throws IllegalAccessException {
        for (Class<?> clazz : componentClasses) {
            Class<?>[] interfaces = clazz.getInterfaces();
            if (interfaces.length == 0) {
                interfaceImplementationsMap.put(clazz, Collections.singletonList(clazz));
            } else {
                for (Class<?> i : interfaces) {
                    registerImplementation(i, clazz);
                }
            }

            try {
                Constructor<?> autowiredConstructor = null;
                Constructor<?> noArgConstructor = null;
                Constructor<?>[] constructors = clazz.getDeclaredConstructors();
                for (Constructor<?> constructor : constructors) {
                    if (constructor.isAnnotationPresent(Autowired.class)) {
                        autowiredConstructor = constructor;
                        break;
                    } else if (constructor.getParameterCount() == 0) {
                        noArgConstructor = constructor;
                    }
                }

                if (autowiredConstructor != null) {
                    Object instance = autowiredConstructor.newInstance(resolveConstructorParameters(autowiredConstructor));
                    applicationInstanceCache.put(clazz, instance);
                } else if (noArgConstructor != null) {
                    Object instance = noArgConstructor.newInstance();
                    applicationInstanceCache.put(clazz, instance);
                } else {
                    throw new RuntimeException("No suitable constructor found for class " + clazz.getName());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (Class<?> clazz : componentClasses) {
            this.autowiredField(clazz);
        }
    }

    private Object[] resolveConstructorParameters(Constructor<?> constructor) throws IllegalAccessException {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] dependencies = new Object[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> paramType = parameterTypes[i];
            dependencies[i] = getOrCreateInstance(paramType);
        }
        return dependencies;
    }

    private Object getOrCreateInstance(Class<?> clazz) throws IllegalAccessException {
        if (applicationInstanceCache.containsKey(clazz)) {
            return applicationInstanceCache.get(clazz);
        }

        if (clazz.isInterface()) {
            List<Class<?>> implementations = interfaceImplementationsMap.get(clazz);
            if (implementations == null || implementations.isEmpty()) {
                throw new RuntimeException("No implementation found for interface " + clazz.getName());
            } else if (implementations.size() > 1) {
                throw new RuntimeException("Multiple implementations found for interface '" + clazz.getName() +
                        "'. Use @Qualifier to specify the desired implementation.");
            }
            clazz = implementations.get(0);
        }

        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        Constructor<?> autowiredConstructor = null;
        Constructor<?> noArgConstructor = null;
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Autowired.class)) {
                autowiredConstructor = constructor;
                break;
            } else if (constructor.getParameterCount() == 0) {
                noArgConstructor = constructor;
            }
        }

        Object instance;
        try {
            if (autowiredConstructor != null) {
                instance = autowiredConstructor.newInstance(resolveConstructorParameters(autowiredConstructor));
            } else if (noArgConstructor != null) {
                instance = noArgConstructor.newInstance();
            } else {
                throw new RuntimeException("No suitable constructor found for class " + clazz.getName());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of " + clazz.getName(), e);
        }

        applicationInstanceCache.put(clazz, instance);
        return instance;
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
            throw new RuntimeException("No instance found for " + clazz.getName() + " in cache.");
        }

        Object value = applicationInstanceCache.get(implementation);
        if (value == null) {
            value = getOrCreateInstance(implementation);
        }
        field.set(instance, value);
    }

    private void registerImplementation(Class<?> i, Class<?> impl) {
        if (!interfaceImplementationsMap.containsKey(i)) {
            interfaceImplementationsMap.put(i, new ArrayList<>( Collections.singletonList(impl)));
        } else {
            interfaceImplementationsMap.get(i).add(impl);
        }
    }
}