package com.codex.testing.services.impl;

import com.codex.framework.annotations.Autowired;
import com.codex.framework.annotations.Component;
import com.codex.testing.UserAccountClientComponent;
import com.codex.testing.services.AccountService;
import com.codex.testing.services.UserService;

@Component
public class AccountServiceImpl implements AccountService  {


    @Override
    public Long getAccountNumber (  ) {
        return 12345689L;
    }

}
