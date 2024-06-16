package org.qbychat.backend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.qbychat.backend.entity.Account;
import org.qbychat.backend.entity.RestBean;
import org.qbychat.backend.entity.Roles;
import org.qbychat.backend.service.impl.AccountServiceImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/user")
@Log4j2
public class UserController {
    @Resource
    private AccountServiceImpl accountService;

    @Resource
    BCryptPasswordEncoder passwordEncoder;

    @GetMapping("whoami")
    public RestBean<String> whoAmI(HttpServletRequest request) {
        return RestBean.success(request.getUserPrincipal().getName());
    }

    @PostMapping("/register")
    public RestBean<String> register(@RequestParam String username, @RequestParam String email, @RequestParam String password) {
        Account account = new Account();
        account.setUsername(username);
        account.setEmail(email);
        account.setNickname(username);
        account.setRegisterTime(LocalDateTime.now());
        account.setRole(Roles.USER);
        account.setPassword(passwordEncoder.encode(password));
        if (accountService.findAccountByNameOrEmail(account.getUsername()) != null) {
            return RestBean.failure(401, "User exist.");
        }
        if (accountService.findAccountByNameOrEmail(account.getEmail()) != null) {
            return RestBean.failure(401, "Email exist.");
        }
        log.info("User " + account.getUsername() + " was registered.");
        accountService.registerAccount(account);
        return RestBean.success("User registered.");
    }
}
