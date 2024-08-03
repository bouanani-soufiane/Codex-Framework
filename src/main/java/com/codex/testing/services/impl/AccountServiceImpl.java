package com.codex.testing.services.impl;

import com.codex.testing.services.AccountService;

public class AccountServiceImpl implements AccountService {
    @Override
    public Long getAccountNumber ( String userName ) {
        return 12345689L;
    }
}
