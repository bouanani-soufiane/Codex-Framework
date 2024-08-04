package com.codex;

import com.codex.framework.AnnotationScanner;
import com.codex.framework.Injector;
import com.codex.framework.annotations.Component;

import java.util.Collection;

public class Main {
    public static void main(String[] args) {
        Injector injector = new Injector();
        injector.initFramework(Main.class);
    }

}

