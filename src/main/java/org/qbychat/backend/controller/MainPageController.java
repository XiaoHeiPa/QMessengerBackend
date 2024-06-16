package org.qbychat.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class MainPageController {
    @GetMapping("/")
    @ResponseBody
    public String index() {
        return "QMessengerBackend"+System.currentTimeMillis();
    }
}
