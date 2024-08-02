package com.codex;

import org.burningwave.core.assembler.ComponentContainer;
import org.burningwave.core.assembler.ComponentSupplier;
import org.burningwave.core.classes.ClassCriteria;
import org.burningwave.core.classes.ClassHunter;
import org.burningwave.core.classes.SearchConfig;

import java.util.Collection;
public class Main {
    public static void main(String[] args) {
        Collection<Class<?>> classes = Main.find();
        Main.printClassNames(classes);

        System.out.println("here : " + classes);
    }

    public static Collection<Class<?>> find() {
        ComponentSupplier componentSupplier = ComponentContainer.getInstance();
        ClassHunter classHunter = componentSupplier.getClassHunter();

        SearchConfig searchConfig = SearchConfig.byCriteria(
                ClassCriteria.create().allThoseThatMatch((cls) -> {
                    return cls.getPackage().getName().matches(".*springframework.*");
                })
        );

        try (ClassHunter.SearchResult searchResult = classHunter.findBy(searchConfig)) {
            return searchResult.getClasses();
        }
    }

    public static void printClassNames(Collection<Class<?>> classes) {
        for (Class<?> cls : classes) {
            System.out.println(cls.getName());
        }
    }
}

