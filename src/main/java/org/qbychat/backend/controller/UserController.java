package org.qbychat.backend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.qbychat.backend.entity.*;
import org.qbychat.backend.entity.dto.FriendDTO;
import org.qbychat.backend.service.impl.AccountServiceImpl;
import org.qbychat.backend.service.impl.EmailServiceImpl;
import org.qbychat.backend.service.impl.FriendsServiceImpl;
import org.qbychat.backend.service.impl.GroupsServiceImpl;
import org.qbychat.backend.utils.Const;
import org.qbychat.backend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

import static org.qbychat.backend.QMessengerBackendApplication.CONFIG_DIR;
import static org.qbychat.backend.utils.Const.ACCOUNT_VERIFY;

@Log4j2
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RedisTemplate<String, String> stringRedisTemplate;

    @Resource
    private AccountServiceImpl accountService;
    @Resource
    private GroupsServiceImpl groupsService;

    @Resource
    private BCryptPasswordEncoder passwordEncoder;
    @Resource
    private FriendsServiceImpl friendsService;

    @Resource
    private JwtUtils jwtUtils;
    @Resource
    private EmailServiceImpl emailService;

    @Value("${messenger.verify.email-verify-url}")
    String verifyUrl;

    @Resource
    @Qualifier("jwtDecoder")
    private JwtDecoder jwtDecoder;

    @GetMapping("/whoami")
    public RestBean<String> whoAmI(HttpServletRequest request) {
        return RestBean.success(request.getUserPrincipal().getName());
    }

    @GetMapping("/account")
    public RestBean<Account> account(HttpServletRequest request) {
        String name = request.getUserPrincipal().getName();
        return RestBean.success(accountService.findAccountByNameOrEmail(name));
    }

    @PostMapping("/account/username")
    public RestBean<String> changeUsername(@RequestParam("value") String username, @NotNull HttpServletRequest request) {
        Account user = accountService.findAccountByNameOrEmail(request.getUserPrincipal().getName());
        if (accountService.findAccountByNameOrEmail(username) != null || groupsService.hasGroup(username)) {
            return RestBean.failure(409, "Username was taken.");
        }
        accountService.updateUsername(user, username);
        // logout current client
        if (jwtUtils.invalidateJwt(request.getHeader("Authorization"))) {
            return RestBean.success("Username changed.");
        }
        return RestBean.failure(500, "username changed, but failed to logout.");
    }

    @PostMapping("/account/nickname")
    public RestBean<String> changeNickname(@RequestParam("nickname") String nickname, @NotNull HttpServletRequest request) {
        Account user = accountService.findAccountByNameOrEmail(request.getUserPrincipal().getName());
        accountService.updateNickname(user, nickname);
        return RestBean.success("Nickname changed.");
    }

    @PostMapping("/account/password")
    public RestBean<String> changePassword(@RequestParam("value") String password, @NotNull HttpServletRequest request) {
        Account user = accountService.findAccountByNameOrEmail(request.getUserPrincipal().getName());
        accountService.updatePassword(user, passwordEncoder.encode(password));
        // logout current client
        if (jwtUtils.invalidateJwt(request.getHeader("Authorization"))) {
            return RestBean.success("Password changed.");
        }
        return RestBean.failure(500, "Password changed, but failed to logout.");
    }

    @GetMapping("/query")
    public RestBean<Account> query(@RequestParam Integer id) {
        Account account = accountService.findAccountById(id);
        if (account == null) {
            return RestBean.failure(404, "Account not found");
        }
        return RestBean.success(account);
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
        newAccount.setRegisterTime(new Date().getTime());
        UUID newAccountUuid = UUID.randomUUID();
        redisTemplate.opsForValue().set(ACCOUNT_VERIFY + newAccountUuid, newAccount);
        Email verifyEmail = new Email();
        verifyEmail.setTo(email);
        verifyEmail.setSubject("QvQ Messenger Verification");
        verifyEmail.setContent("This is a verify Email. Please click the verify url to continue registration: " + verifyUrl + newAccountUuid);
        String emailReturn = emailService.sendVerifyEmail(verifyEmail);
        if (Objects.equals(emailReturn, "Succeed!")) {
            log.info("New account tried to register with email: {} UUID: {}", newAccount.getEmail(), newAccountUuid);
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

    @GetMapping("/friends/list")
    public RestBean<List<FriendDTO>> getFriends(@NotNull HttpServletRequest request) {
        Account user = accountService.findAccountByNameOrEmail(request.getUserPrincipal().getName());
        List<Account> friends = friendsService.getFriendsWithAccount(user);
        List<FriendDTO> friendDTOs = new ArrayList<>();
        for (Account friend : friends) {
            friendDTOs.add(new FriendDTO(friend.getId(), friend.getUsername(), friend.getNickname()));
        }
        return RestBean.success(friendDTOs);
    }

    @GetMapping("/groups/list")
    public RestBean<List<Group>> getGroups(@NotNull HttpServletRequest request) {
        Account user = accountService.findAccountByNameOrEmail(request.getUserPrincipal().getName());
        List<Group> groups = groupsService.queryJoinedGroups(user);
        return RestBean.success(groups);
    }

    @GetMapping("/avatar")
    public void avatar(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws Exception{
        String name = request.getUserPrincipal().getName();
        Account user = accountService.findAccountByNameOrEmail(name);
        Integer id = user.getId();
        File avatarFile = new File(CONFIG_DIR, "avatar/users/" + id + ".png");
        log.info("Get avatar {}", id);
        if (!avatarFile.exists()) {
            response.sendRedirect("https://http.cat/404.png");
            return;
        }
        response.setHeader("Content-Type", "image/png");
        try (FileInputStream stream = new FileInputStream(avatarFile)) {
            IOUtils.copy(stream, response.getOutputStream());
        }
    }

    @PostMapping("/fcm/token")
    public RestBean<String> updateFCMToken(@RequestParam("newToken") String token, HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();
        Account user = accountService.findAccountByNameOrEmail(username);
        stringRedisTemplate.opsForValue().set(Const.FCM_TOKEN + user.getId(), token);
        return RestBean.success("Token updated.");
    }

    @GetMapping("/fcm/token")
    public RestBean<String> getFCMToken(HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();
        Account user = accountService.findAccountByNameOrEmail(username);
        String token = stringRedisTemplate.opsForValue().get(Const.FCM_TOKEN + user.getId());
        return RestBean.success(token);
    }
}
