package com.codex.testing.services;

import com.codex.framework.annotations.Component;

@Component
public interface AccountService {
    Long getAccountNumber(String userName);

}
