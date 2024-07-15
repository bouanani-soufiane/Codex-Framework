package com.codex;import java.util.Collection;

import org.burningwave.core.assembler.ComponentContainer;
import org.burningwave.core.assembler.ComponentSupplier;
import org.burningwave.core.classes.ClassHunter;
import org.burningwave.core.classes.JavaClass;
import org.burningwave.core.classes.SearchConfig;
import org.burningwave.core.io.FileSystemItem;

public class Finder {

    public Collection<Class<?>> find() {
        ComponentSupplier componentSupplier = ComponentContainer.getInstance();
        ClassHunter classHunter = componentSupplier.getClassHunter();

        SearchConfig searchConfig = SearchConfig.create().addFileFilter(
                FileSystemItem.Criteria.forAllFileThat( fileSystemItem -> {
                    JavaClass javaClass = fileSystemItem.toJavaClass();
                    if (javaClass == null) {
                        return false;
                    }
                    String packageName = javaClass.getPackageName();
                    return packageName != null && packageName.contains("s");
                })
        );

        try(ClassHunter.SearchResult searchResult = classHunter.findBy(searchConfig)) {
            return searchResult.getClasses();
        }
    }

}