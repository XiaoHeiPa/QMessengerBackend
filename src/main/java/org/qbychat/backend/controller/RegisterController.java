package org.qbychat.backend.controller;

import jakarta.annotation.Resource;
import org.qbychat.backend.entity.Account;
import org.qbychat.backend.entity.RestBean;
import org.qbychat.backend.service.impl.AccountServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/user")
public class RegisterController {
    @Resource
    private AccountServiceImpl accountService;

    @PostMapping("/register")
    public RestBean<String> registerUser(@RequestParam Account account) {
        if (accountService.findAccountByNameOrEmail(account.getUsername()) == null) {
            return RestBean.failure(401, "User exist.");
        }
        if (accountService.findAccountByNameOrEmail(account.getEmail()) == null) {
            return RestBean.failure(401, "Email exist.");
        }
        accountService.registerAccount(account);
        return RestBean.success("User registered.");
    }
}
