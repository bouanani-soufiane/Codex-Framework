package com.codex.testing.services.impl;

import com.codex.framework.annotations.Component;
import com.codex.testing.services.UserService;

@Component
public class UserServiceImpl implements UserService {

    @Override
    public void getUserAccount() {
        System.out.println("hahowa ===================================================== yes");
    }
}
