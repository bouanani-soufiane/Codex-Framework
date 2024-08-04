package com.codex.testing;

import com.codex.framework.annotations.Autowired;
import com.codex.framework.annotations.Component;
import com.codex.framework.annotations.Qualifier;
import com.codex.testing.services.UserService;
import com.codex.testing.services.impl.UserServiceImpl;

@Component
public class UserAccountClientComponent {

    @Autowired
    @Qualifier(UserServiceImpl.class)
    private UserService userService;

    public void hello() {
        userService.getUserAccount();
    }
}


