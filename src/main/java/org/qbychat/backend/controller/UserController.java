package org.qbychat.backend.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.qbychat.backend.entity.Account;
import org.qbychat.backend.entity.Email;
import org.qbychat.backend.entity.RestBean;
import org.qbychat.backend.entity.Roles;
import org.qbychat.backend.service.impl.AccountServiceImpl;
import org.qbychat.backend.service.impl.EmailServiceImpl;
import org.qbychat.backend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Resource
    private JwtUtils jwtUtils;
//    @Resource
private final EmailServiceImpl emailService = new EmailServiceImpl();

    @Value("${messenger.verify.email-verify-url}")
    String verifyUrl;

    @Qualifier("jwtDecoder")
    @Autowired
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
        newAccount.setRole("user");
        newAccount.setUsername(name);
        newAccount.setEmail(email);
        newAccount.setPassword(passwordEncoder.encode(password));
        newAccount.setNickname(name);
        newAccount.setRegisterTime(new Date());
        UUID newAccountUuid = UUID.randomUUID();
        redisTemplate.opsForValue().set(String.valueOf(newAccountUuid), newAccount);
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
            accountService.registerAccount(registerAccount);
            assert registerAccount != null;
            log.info("Account {} has been registered.", registerAccount.getUsername());
            return RestBean.success("Register success.");
        } else {
            log.warn("Someone try to register with uuid: {} but it's not exits.", uuid);
            return RestBean.failure(401, "uuid not found!");
        }
    }

    @PostMapping("/change-user-role")
    public RestBean<String> changeUserRole(@RequestHeader("Authorization") Header header, @RequestParam String username, @RequestParam String role) {
        String token = header.getValues().get(0);
        if (jwtUtils.invalidateJwt(token)) {
            DecodedJWT decodedJWT = (DecodedJWT) jwtDecoder.decode(token);
            Account postAccount = accountService.findAccountByNameOrEmail(jwtUtils.toUser(decodedJWT).getUsername());
            if (Objects.equals(postAccount.getRole(), Roles.ADMIN)) {
                Account changeAccount = accountService.findAccountByNameOrEmail(username);
                if (changeAccount != null) {
                    accountService.changeAccountRole(changeAccount, role);
                    return RestBean.success("Change successful!");
                } else {
                    return RestBean.failure(401, "Change user not found!");
                }
            } else {
                return RestBean.failure(401, "You aren't admin!");
            }
        } else {
            return RestBean.forbidden("Your token is invalid!");
        }
    }
}
