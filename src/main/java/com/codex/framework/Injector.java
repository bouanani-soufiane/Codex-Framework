package com.codex.framework;

import com.codex.framework.annotations.Autowired;
import com.codex.framework.annotations.Component;
import com.codex.framework.annotations.Qualifier;

import java.lang.reflect.Field;
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
        for (Class<?> clazz : componentClasses) {
            this.autowiredField(clazz);
        }
    }


    private void autowiredField(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields){
            if (!field.isAnnotationPresent(Qualifier.class) && field.isAnnotationPresent(Autowired.class) ){
                if(!interfaceImplementationsMap.containsKey(field.getType())){
                    throw new RuntimeException("not registred, check in you miss @Componet ");
                }else if(interfaceImplementationsMap.containsKey(field.getType())){
                        if (interfaceImplementationsMap.get(field.getType()).size() > 1){
                            throw new RuntimeException("need to specify exactly which implementation, found more than one implementation for "
                                    + field.getType()+ "use @Qualifier or @Autowired to specify multiple implementations" );
                        }else {
                            System.out.println("ok good now");
                        }
                }
            }
        }
    }


    private void injectField(Class<?> clazz , Field field){
        field.setAccessible(true);

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
