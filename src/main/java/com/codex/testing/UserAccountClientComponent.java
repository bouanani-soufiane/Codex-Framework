package com.codex.testing;

import com.codex.framework.annotations.Autowired;
import com.codex.framework.annotations.Component;
import com.codex.framework.annotations.Qualifier;
import com.codex.testing.services.AccountService;
import com.codex.testing.services.UserService;

@Component
public class UserAccountClientComponent {
    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier(value = "accountServiceImpl")
    private AccountService accountService;

    public void displayUserAccount() {
        String username = userService.getUserName();
        Long accountNumber = accountService.getAccountNumber(username);
        System.out.println("\n\tUser Name: " + username + "\n\tAccount Number: " + accountNumber);
    }
    public void hello() {
        System.out.println("i made it" );
    }
}
