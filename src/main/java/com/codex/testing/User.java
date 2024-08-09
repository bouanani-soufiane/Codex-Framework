package com.codex.testing;

import com.codex.framework.DIContainer.annotations.Component;
import com.codex.testing.interfaces.IUser;

@Component
public class User implements IUser {
    public String name = "soufiane";

    @Override
    public String getName () {
        return name;
    }
}
