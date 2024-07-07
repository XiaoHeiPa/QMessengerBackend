package org.qbychat.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;

import static org.qbychat.backend.QMessengerBackendApplication.CONFIG_DIR;

@RestController
@RequestMapping("/avatar")
public class AvatarController {
    @GetMapping("query")
    public void query(HttpServletRequest request, HttpServletResponse response, @RequestParam Integer id, @RequestParam int isUser) throws Exception {
        File avatarFile = new File(CONFIG_DIR, "avatar/" + (isUser == 1 ? "users" : "groups") + "/" + id + ".png");
        if (!avatarFile.exists()) {
            response.sendRedirect("https://http.cat/404.png");
            return;
        }
        response.setHeader("Content-Type", "image/png");
        try (FileInputStream stream = new FileInputStream(avatarFile)) {
            IOUtils.copy(stream, response.getOutputStream());
        }
    }
}
