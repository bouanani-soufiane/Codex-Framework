package com.codex.framework;

import com.codex.framework.annotations.Autowired;
import com.codex.framework.annotations.Component;
import com.codex.framework.annotations.Qualifier;
import com.codex.framework.utils.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
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
        this.inject(components);
    }

    private void inject(Collection<Class<?>> components) throws NoSuchMethodException {
        for (Class<?> component : components) {
            initializeBean(component);
        }
    }

    private void initializeBean(Class<?> component) throws NoSuchMethodException {
        Constructor<?>[] constructors = component.getDeclaredConstructors();
        Field[] fields = component.getDeclaredFields();

        for (Constructor<?> constructor : constructors) {
            if (instances.containsKey(component)) {
                break;
            }
            createInstance(constructor);
        }

        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Object dependency = resolveDependency(field.getType(), field.getAnnotation(Qualifier.class));
                injectField(field, dependency);
            }
        }
    }

    private void createInstance(Constructor<?> constructor) throws NoSuchMethodException {
        Object[] args = null;

        if (constructor.isAnnotationPresent(Autowired.class)) {
            Parameter[] parameters = constructor.getParameters();
            Qualifier[] qualifiers = constructor.getAnnotationsByType(Qualifier.class);

            args = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Class<?> paramType = parameters[i].getType();
                Qualifier qualifier = i < qualifiers.length ? qualifiers[i] : null;

                args[i] = resolveDependency(paramType, qualifier);
            }
        }

        try {
            Object instance = constructor.newInstance(args);
            instances.put(constructor.getDeclaringClass(), instance);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Error creating instance", e);
        }
    }

    private Object resolveDependency(Class<?> type, Qualifier qualifier) {
        if (!type.isInterface()) {
            return getOrCreate(type);
        } else {
            List<Class<?>> implementations = bindingMap.get(type);

            if (implementations == null || implementations.isEmpty()) {
                throw new RuntimeException("No implementation found for " + type);
            }

            if (implementations.size() > 1) {
                if (qualifier == null) {
                    throw new RuntimeException("Multiple implementations found for " + type + ". Please specify a @Qualifier.");
                }

                Class<?> qualifiedClass = qualifier.value();
                if (!implementations.contains(qualifiedClass)) {
                    throw new RuntimeException("Qualifier " + qualifiedClass + " is not a valid implementation for " + type);
                }
                return getOrCreate(qualifiedClass);
            } else {
                return getOrCreate(implementations.getFirst());
            }
        }
    }


    private void injectField(Field field, Object instance) {
        try {
            field.setAccessible(true);
            Object componentInstance = instances.get(field.getDeclaringClass());
            field.set(componentInstance, instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error injecting field " + field.getName() + " in " + field.getDeclaringClass().getName(), e);
        }
    }

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

    public Object getBean(Class<?> clazz) {
        return instances.get(clazz);
    }
}
