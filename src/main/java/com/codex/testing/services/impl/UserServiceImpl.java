package com.codex.testing.services.impl;

import com.codex.framework.annotations.Component;
import com.codex.testing.services.UserService;

@Component
public class UserServiceImpl implements UserService {
    @Override
    public String getUserName () {
        return "soufiane";
    }
}
