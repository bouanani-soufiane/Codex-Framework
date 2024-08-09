package com.codex.testing.Components;

import com.codex.framework.DIContainer.annotations.Component;
import com.codex.testing.Components.interfaces.IUser;

@Component
public class User implements IUser {
    public String name = "soufiane";

    @Override
    public String getName () {
        return name;
    }
}
