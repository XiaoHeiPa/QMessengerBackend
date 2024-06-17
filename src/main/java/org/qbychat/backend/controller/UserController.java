package org.qbychat.backend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.qbychat.backend.entity.Account;
import org.qbychat.backend.entity.Config;
import org.qbychat.backend.entity.RestBean;
import org.qbychat.backend.entity.VerifyEmail;
import org.qbychat.backend.service.impl.AccountServiceImpl;
import org.qbychat.backend.utils.ConfigUtils;
import org.qbychat.backend.utils.EmailUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@Log4j2
public class UserController {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private AccountServiceImpl accountService;

    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/whoami")
    public RestBean<String> whoAmI(HttpServletRequest request) {
        return RestBean.success(request.getUserPrincipal().getName());
    }

    @PostMapping("/register")
    public RestBean<String> registerUser(@RequestParam("username") String name, @RequestParam("email") String email, @RequestParam String password) {
        if (accountService.findAccountByNameOrEmail(name) != null) {
            return RestBean.failure(401, "User exist.");
        }
        if (accountService.findAccountByNameOrEmail(email) != null) {
            return RestBean.failure(401, "Email exist.");
        }
        Account newAccount = new Account();
        newAccount.setRole("user");
        newAccount.setUsername(name);
        newAccount.setEmail(email);
        newAccount.setPassword(passwordEncoder.encode(password));
        newAccount.setNickname(name);
        newAccount.setRegisterTime(LocalDateTime.now());
        UUID newAccountUuid = UUID.randomUUID();
        redisTemplate.opsForValue().set(String.valueOf(newAccountUuid), newAccount);
        EmailUtils emailUtils = new EmailUtils();
        VerifyEmail verifyEmail = new VerifyEmail();
        ConfigUtils configUtils = new ConfigUtils();
        Config config = configUtils.loadConfig();
        verifyEmail.setTo(email);
        verifyEmail.setSubject("Verify Email");
        verifyEmail.setContent("Verify Email, Your verify url: " + config.getVerify().getEmail_verify_url() + newAccountUuid);
        String emailReturn = emailUtils.sendVerifyEmail(verifyEmail);
        if (Objects.equals(emailReturn, "Succeed!")) {
            log.info("New account try to register with email: {} UUID: {}", newAccount.getEmail(), newAccountUuid);
            return RestBean.success("请从您的邮箱中获取验证链接！");
        } else {
            log.error(emailReturn);
            return RestBean.failure(500, "服务器发送邮件失败，请稍后再试，或与管理员取得联系！");
        }
    }

    @GetMapping("/apply-register")
    public RestBean<String> applyRegister(@RequestParam("uuid") String uuid) {
        UUID userUuid = UUID.fromString(uuid);
        if (Boolean.TRUE.equals(redisTemplate.hasKey(userUuid.toString()))) {
            Account registerAccount = (Account) redisTemplate.opsForValue().getAndDelete(uuid);
            accountService.registerAccount(registerAccount);
            assert registerAccount != null;
            log.info("Account {} has been registered.", registerAccount.getUsername());
            return RestBean.success("Register success.");
        } else {
            log.warn("Someone try to register with uuid: {} but it's not exits.", uuid);
            return RestBean.failure(401, "uuid not found!");
        }
    }
}
