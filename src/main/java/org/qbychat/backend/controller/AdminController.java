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

import java.util.Date;

@Log4j2
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Resource
    AccountServiceImpl accountService;
    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/ping")
    public String ping() {
        return "Hello admin";
    }

    @PostMapping("/update-role")
    public RestBean<String> updateRole(@RequestParam("username") String username, @RequestParam("role") Roles role, HttpServletRequest request) {
        if (request.getUserPrincipal().getName().equals(username)) {
            return RestBean.failure(403, "You cannot modify your role.");
        }
        Account account = accountService.findAccountByNameOrEmail(username);
        accountService.updateRole(account, role);
        return RestBean.success();
    }

    @PostMapping("/register")
    public RestBean<String> register(@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("email") String email, @RequestParam(value = "nickname", required = false) String nickname, @RequestParam(value = "role", required = false) Roles role, HttpServletRequest request) {
        if (accountService.findAccountByNameOrEmail(username) != null) {
            return RestBean.failure(409, "Account already exists.");
        }
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(passwordEncoder.encode(password));
        account.setEmail(email);
        account.setRegisterTime(new Date());
        if (role != null) {
            account.setRole(role.name());
        } else {
            account.setRole(Roles.USER.name());
        }
        if (nickname != null) {
            account.setNickname(nickname);
        } else {
            account.setNickname(username);
        }
        log.info("Force register account: {}", account);
        if (accountService.save(account)) {
            return RestBean.success();
        }
        return RestBean.failure(500, "Unable to save account. (SQL)");
    }
}
