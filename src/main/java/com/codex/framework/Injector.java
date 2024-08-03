package com.codex.framework;

public class Injector {

    private static Injector injector;

    public static void startApplication( Class<?> mainClass) {
        try {
            synchronized (Injector.class) {
                if (injector == null) {
                    injector = new Injector();
                    injector.initFramework(mainClass);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initFramework ( Class<?> mainClass ) {

    }
}
