package org.qbychat.backend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.qbychat.backend.entity.Account;
import org.qbychat.backend.entity.Invitation;
import org.qbychat.backend.entity.RestBean;
import org.qbychat.backend.entity.Role;
import org.qbychat.backend.service.impl.AccountServiceImpl;
import org.qbychat.backend.service.impl.GroupsServiceImpl;
import org.qbychat.backend.utils.Const;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import static org.qbychat.backend.QMessengerBackendApplication.CONFIG_DIR;

@Log4j2
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Resource
    AccountServiceImpl accountService;
    @Resource
    private BCryptPasswordEncoder passwordEncoder;
    @Resource
    GroupsServiceImpl groupsService;
    @Resource
    RedisTemplate<String, Object> invitationRedisTemplate;
    @Value("${messenger.registration.invitation.expire}")
    private int invitationExpire;
    @Value("${messenger.registration.allow}")
    private boolean allowRegistration;

    @GetMapping("/ping")
    public String ping() {
        return "Hello admin";
    }

    @PostMapping("/manage/user/{user}/role")
    public RestBean<String> updateRole(@PathVariable("user") String username, @RequestParam("role") Role role, HttpServletRequest request) {
        if (request.getUserPrincipal().getName().equals(username)) {
            return RestBean.failure(403, "You cannot modify your role.");
        }
        Account account = accountService.findAccountByNameOrEmail(username);
        account.setRole(role);
        accountService.updateUser(account);
        return RestBean.success();
    }

    @PostMapping("/manage/user/{user}/username")
    public RestBean<String> updateUsername(@PathVariable("user") Integer user, @RequestParam String newUsername) {
        if (accountService.findAccountByNameOrEmail(newUsername) != null) return RestBean.failure(409, "Username " + newUsername + " already exists.");
        Account account = accountService.findAccountById(user);
        if (account == null) return RestBean.failure(404, "Account does not exist.");
        account.setUsername(newUsername);
        accountService.updateUser(account);
        return RestBean.success();
    }

    @PostMapping("/manage/user/{user}/nickname")
    public RestBean<String> updateNickname(@PathVariable("user") Integer user, @RequestParam String newNickname) {
        Account account = accountService.findAccountById(user);
        if (account == null) return RestBean.failure(404, "Account does not exist.");
        account.setNickname(newNickname);
        accountService.updateUser(account);
        return RestBean.success();
    }

    @PostMapping("/manage/user/{user}/bio")
    public RestBean<String> updateBio(@PathVariable("user") Integer user, @RequestParam String bio) {
        Account account = accountService.findAccountById(user);
        if (account == null) return RestBean.failure(404, "Account does not exist.");
        account.setBio(bio);
        accountService.updateUser(account);
        return RestBean.success();
    }

    @PostMapping("/manage/user/{user}/avatar")
    public RestBean<String> updateAvatar(@PathVariable("user") String user, HttpServletRequest request) throws Exception {
        Account account = accountService.findAccountByNameOrEmail(user);
        if (account == null) return RestBean.failure(404, "Account does not exist.");
        File avatarFile = new File(CONFIG_DIR, "avatar/" + "users" + "/" + account.getId() + ".png");
        try (FileOutputStream stream = new FileOutputStream(avatarFile)) {
            IOUtils.copy(request.getInputStream(), stream);
        }
        return RestBean.success();
    }

    @GetMapping("/register/invite")
    public RestBean<Invitation> generateInviteCode(HttpServletRequest request) {
        if (allowRegistration) return RestBean.failure(405, "Invite code is current disabled in application.yml");
        Account admin = accountService.findAccountByNameOrEmail(request.getUserPrincipal().getName());
        UUID uuid = UUID.randomUUID();
        Invitation invitation = new Invitation();
        invitation.setAdminId(admin.getId());
        invitation.setCode(uuid.toString());
        invitation.setExpire(new Date().getTime() + ((long) invitationExpire * 24 * 60 * 60));
        invitationRedisTemplate.opsForValue().set(Const.INVITATION + uuid, invitation);
        return RestBean.success(invitation);
    }

    /**
     * Force register
     * */
    @PostMapping("/register")
    public RestBean<String> register(@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("email") String email, @RequestParam(value = "nickname", required = false) String nickname, @RequestParam(value = "role", required = false) Role role, HttpServletRequest request) {
        if (accountService.hasUser(username)) {
            return RestBean.failure(409, "Account already exists.");
        }
        if (groupsService.hasGroup(username)) {
            return RestBean.failure(409, "Name" + username + " had taken by a group.");
        }
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(passwordEncoder.encode(password));
        account.setEmail(email);
        account.setRegisterTime(new Date().getTime());
        account.setRole(Objects.requireNonNullElse(role, Role.USER));
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

    @PostMapping("/ban")
    public RestBean<String> ban(@RequestParam("username") String username, HttpServletRequest request) {
        Account account = accountService.findAccountByNameOrEmail(username);
        if (!account.isActive()) {
            return RestBean.failure(409, "Account is already disabled.");
        }
        Account me = accountService.findAccountByNameOrEmail(request.getUserPrincipal().getName());
        if (account.equals(me)) {
            return RestBean.failure(403, "You cannot ban yourself. Here is not Minecraft.");
        }
        account.setActive(false);
        accountService.updateUser(account);
        return RestBean.success("User " + username + " has been banned.");
    }

    @PostMapping("pardon")
    public RestBean<String> pardon(@RequestParam("username") String username, HttpServletRequest request) {
        Account account = accountService.findAccountByNameOrEmail(username);
        if (account.isActive()) {
            return RestBean.failure(409, "Account is already active.");
        }
        account.setActive(true);
        accountService.updateUser(account);
        return RestBean.success("User " + username + " has been pardoned.");
    }
}
