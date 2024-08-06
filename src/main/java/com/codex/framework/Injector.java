package com.codex.framework;
import com.codex.framework.annotations.Autowired;
import com.codex.framework.annotations.Component;
import com.codex.framework.annotations.Qualifier;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;

public class Injector {

    AnnotationScanner scanner;
    Utils utils;
    Collection<Class<?>> components;

    private final Map<Class<?>, List<Class<?>>> bindingMap = new HashMap<>();
    private final Map<Class<?>, Object> instances = new HashMap<>();

    public Injector() {
        this.utils = new Utils();
        this.scanner = new AnnotationScanner(Component.class);
        this.components = scanner.find("com/codex");
    }

    public void initFramework(Class<?> mainClass) throws IllegalAccessException, NoSuchMethodException {
        this.bindInterfaceToClassImpls(components);
        this.initializeBeans(components);
    }

    /**
     * Initializes and caches instances of the provided component classes,
     * using the appropriate constructors for dependency injection.
     *
     * @param components a collection of component classes to initialize
     */

    private void initializeBeans(Collection<Class<?>> components) throws NoSuchMethodException {
        for (Class<?> component : components) {
            Constructor<?>[] constructors = component.getDeclaredConstructors();
            for (Constructor<?> constructor : constructors) {
                if (constructor.isAnnotationPresent(Autowired.class)) {
                    withAutowiredConstructor(constructor);
                } else {
                    withDefaultConstructor(constructor);
                }
            }
        }
    }

    /**
     * Creates an instance of a class using a constructor annotated with `@Autowired`.
     *
     * @param constructor the constructor annotated with `@Autowired`
     */

    private void withAutowiredConstructor(Constructor<?> constructor) throws NoSuchMethodException {
        Parameter[] parameters = constructor.getParameters();
        Qualifier[] qualifiers = constructor.getAnnotationsByType(Qualifier.class);

        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Class<?> paramType = parameters[i].getType();
            if (!paramType.isInterface()) {
                args[i] = getOrCreate(paramType);
            } else {
                if (!bindingMap.containsKey(paramType) || bindingMap.get(paramType).isEmpty()) {
                    throw new RuntimeException("No implementation found for " + paramType);
                }
                if (bindingMap.get(paramType).size() > 1) {
                    Class<?> implClass = checkQualifier(paramType, qualifiers);
                    args[i] = getOrCreate(implClass);
                } else {
                    args[i] = getOrCreate(bindingMap.get(paramType).getFirst());
                }
            }
        }
        try {
            Object instance = constructor.newInstance(args);
            instances.put(constructor.getDeclaringClass(), instance);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Error creating instance", e);
        }
    }

    /**
     * Creates an instance of a class using default constructor
     *
     * @param constructor the default constructor
     */

    private void withDefaultConstructor(Constructor<?> constructor) {
        try {
            Object instance = constructor.newInstance();
            instances.put(constructor.getDeclaringClass(), instance);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Error creating instance", e);
        }
    }

    /**
     * Checks if there is an implementation of the specified interface that matches any of the given qualifiers.
     *
     * @param type the interface type to check for implementations
     * @param qualifiers the array of qualifiers to match against the implementations
     * @return the class type of the matching implementation, if found
     */

    private Class<?> checkQualifier(Class<?> type, Qualifier[] qualifiers) {
        for (Qualifier qualifier : qualifiers) {
            List<Class<?>> implementations = bindingMap.get(type);
            if (implementations != null && implementations.contains(qualifier.value())) {
                return qualifier.value();
            }
        }
        throw new RuntimeException("No implementation found for " + type + " with the specified qualifiers.");
    }

    /**
     * Retrieves an existing instance from the cache or creates a new one if not present.
     *
     * @param clazz the class type of the instance
     * @return the instance of the specified class
     */

    private Object getOrCreate(Class<?> clazz) {
        if (!instances.containsKey(clazz)) {
            try {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                instances.put(clazz, instance);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                throw new RuntimeException("Error creating instance for " + clazz, e);
            }
        }
        return instances.get(clazz);
    }

    /**
     * Binds each interface to the list of all classes implementing it.
     *
     * @param components a collection of component classes
     */

    private void bindInterfaceToClassImpls(Collection<Class<?>> components) {
        for (Class<?> component : components) {
            Class<?>[] interfaces = component.getInterfaces();
            if (interfaces.length == 0) {
                bindingMap.put(component, Collections.singletonList(component));
            } else {
                for (Class<?> i : interfaces) {
                    if (!bindingMap.containsKey(i)) {
                        bindingMap.put(i, new ArrayList<>(Collections.singletonList(component)));
                    } else {
                        bindingMap.get(i).add(component);
                    }
                }
            }
        }
    }

    /**
     * Provides access to the instances managed by the framework.
     * Acts as the context holder for all created objects.
     *
     * @param clazz the class type of the instance to retrieve
     * @return the instance of the requested class, or null if not found
     */

    public Object getBean(Class<?> clazz) {
        return instances.get(clazz);
    }
}
