package com.codex.testing.services.impl;

import com.codex.framework.annotations.Component;
import com.codex.testing.services.AccountService;
import com.codex.testing.services.UserService;

@Component
public class AccountServiceImpl implements AccountService , UserService {
    @Override
    public Long getAccountNumber ( String userName ) {
        return 12345689L;
    }

    @Override
    public String getUserName () {
        return "";
    }
}
