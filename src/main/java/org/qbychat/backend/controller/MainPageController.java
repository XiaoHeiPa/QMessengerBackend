package org.qbychat.backend.controller;

import org.qbychat.backend.entity.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class MainPageController {
    @GetMapping("/")
    @ResponseBody
    public R<String> index() {
        return R.success(String.format("QMessengerBackend %d", System.currentTimeMillis()));
    }
}
