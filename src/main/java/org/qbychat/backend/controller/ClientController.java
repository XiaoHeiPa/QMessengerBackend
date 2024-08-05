package org.qbychat.backend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.qbychat.backend.entity.ClientVerify;
import org.qbychat.backend.service.impl.ClientVerifyServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientController {
    @Resource
    ClientVerifyServiceImpl clientVerifyService;

    @PostMapping("verify")
    public ClientVerify verify(@RequestParam String sha1, HttpServletResponse response) {
        // 在gradle构建时会向服务器发送update请求, 只需要判断这个sha1是否在白名单即可
        ClientVerify auth = clientVerifyService.auth(sha1);
        if (auth == null) {
            response.setStatus(403);
            return null;
        }
        return auth;
    }
}
