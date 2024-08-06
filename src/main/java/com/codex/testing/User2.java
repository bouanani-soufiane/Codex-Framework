package com.codex.testing;

import com.codex.framework.annotations.Autowired;
import com.codex.framework.annotations.Component;
import com.codex.testing.interfaces.IUser;

@Component
public class User2 {

    public String getName () {
        return "walo";
    }

}
