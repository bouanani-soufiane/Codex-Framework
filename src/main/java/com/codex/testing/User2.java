package com.codex.testing;

import com.codex.framework.DI.annotations.Autowired;
import com.codex.framework.DI.annotations.Component;
import com.codex.testing.interfaces.IUser;

@Component
public class User2 implements IUser {
    @Override
    public String getName () {
        return "walo";
    }

}
