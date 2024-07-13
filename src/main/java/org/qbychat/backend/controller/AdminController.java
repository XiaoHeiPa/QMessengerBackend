package org.qbychat.backend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.qbychat.backend.entity.Account;
import org.qbychat.backend.entity.Invitation;
import org.qbychat.backend.entity.RestBean;
import org.qbychat.backend.entity.Roles;
import org.qbychat.backend.service.impl.AccountServiceImpl;
import org.qbychat.backend.service.impl.GroupsServiceImpl;
import org.qbychat.backend.utils.Const;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

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

    @GetMapping("/register/invite")
    public RestBean<Invitation> generateInviteCode(HttpServletRequest request) {
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
    public RestBean<String> register(@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("email") String email, @RequestParam(value = "nickname", required = false) String nickname, @RequestParam(value = "role", required = false) Roles role, HttpServletRequest request) {
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
        account.setRole(Objects.requireNonNullElse(role, Roles.USER).name());
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
