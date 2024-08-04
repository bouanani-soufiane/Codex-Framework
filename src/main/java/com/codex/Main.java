package com.codex;
import com.codex.framework.Injector;
import com.codex.testing.UserAccountClientComponent;


public class Main {
    public static void main(String[] args) throws IllegalAccessException {
        Injector injector = new Injector();
        injector.initFramework(Main.class);

        UserAccountClientComponent user = (UserAccountClientComponent) injector.getBean(UserAccountClientComponent.class);
        user.hello();

    }

}

