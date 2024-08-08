package com.codex.framework.processors;
import com.codex.framework.annotations.Autowired;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("com.codex.framework.annotations.Autowired")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AutowiredProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // Debug message to verify process method is invoked
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "AutowiredProcessor: process method invoked");

        for (Element element : roundEnv.getElementsAnnotatedWith(Autowired.class)) {
            if (element.getKind() == ElementKind.CONSTRUCTOR) {
                TypeElement enclosingClass = (TypeElement) element.getEnclosingElement();
                List<ExecutableElement> constructors = ElementFilter.constructorsIn(enclosingClass.getEnclosedElements());

                long autowiredCount = constructors.stream()
                        .filter(constructor -> constructor.getAnnotation(Autowired.class) != null)
                        .count();

                if (autowiredCount > 1) {
                    for (Element constructor : constructors) {
                        if (constructor.getAnnotation(Autowired.class) != null) {
                            processingEnv.getMessager().printMessage(
                                    Diagnostic.Kind.ERROR,
                                    "Only one constructor can be annotated with @Autowired in class: " + enclosingClass.getQualifiedName(),
                                    constructor
                            );
                        }
                    }
                }
            }
        }
        return true; // No further processing of this annotation type is necessary
    }
}
