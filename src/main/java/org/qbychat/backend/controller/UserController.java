package org.qbychat.backend.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.qbychat.backend.entity.Account;
import org.qbychat.backend.entity.Email;
import org.qbychat.backend.entity.RestBean;
import org.qbychat.backend.entity.Roles;
import org.qbychat.backend.service.impl.AccountServiceImpl;
import org.qbychat.backend.service.impl.EmailServiceImpl;
import org.qbychat.backend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.header.Header;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import static org.qbychat.backend.utils.Const.ACCOUNT_VERIFY;

@Log4j2
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private AccountServiceImpl accountService;

    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    @Resource
    private JwtUtils jwtUtils;
    //    @Resource
    private final EmailServiceImpl emailService = new EmailServiceImpl();

    @Value("${messenger.verify.email-verify-url}")
    String verifyUrl;

    @Resource
    @Qualifier("jwtDecoder")
    private JwtDecoder jwtDecoder;

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
        newAccount.setRole(Roles.USER.name());
        newAccount.setUsername(name);
        newAccount.setEmail(email);
        newAccount.setPassword(passwordEncoder.encode(password));
        newAccount.setNickname(name);
        newAccount.setRegisterTime(new Date());
        UUID newAccountUuid = UUID.randomUUID();
        redisTemplate.opsForValue().set(ACCOUNT_VERIFY + newAccountUuid, newAccount);
        Email verifyEmail = new Email();
        verifyEmail.setTo(email);
        verifyEmail.setSubject("QbyChat Verify Email");
        verifyEmail.setContent("This is a verify Email. Please click the verify url to continue registration: " + verifyUrl + newAccountUuid);
        String emailReturn = emailService.sendVerifyEmail(verifyEmail);
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
            if (!accountService.save(registerAccount)) {
                log.info("An error happened when registering {}", registerAccount);
                return RestBean.failure(500, "An error happened when registering");
            }
            assert registerAccount != null;
            log.info("Account {} has been registered.", registerAccount.getUsername());
            return RestBean.success("Register success.");
        } else {
            log.warn("Someone try to register with uuid: {} but it's not exits.", uuid);
            return RestBean.failure(401, "uuid not found!");
        }
    }

    @PostMapping("/update-password")
    public RestBean<String> changePassword(@RequestParam("password") String password, @NotNull HttpServletRequest request) {
        Account user = accountService.findAccountByNameOrEmail(request.getUserPrincipal().getName());
        accountService.updatePassword(user, passwordEncoder.encode(password));
        // logout current client
        if (jwtUtils.invalidateJwt(request.getHeader("Authorization"))) {
            return RestBean.success("Password changed.");
        }
        return RestBean.failure(500, "Password changed, but failed to logout.");
    }
}
