package org.qbychat.backend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.qbychat.backend.entity.Account;
import org.qbychat.backend.entity.RestBean;
import org.qbychat.backend.service.impl.AccountServiceImpl;
import org.qbychat.backend.ws.FindDeviceHandler;
import org.qbychat.backend.ws.entity.Location;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/find-device")
public class FindDeviceController {

    @Resource
    private AccountServiceImpl accountService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private FindDeviceHandler handler;

    @GetMapping("device-list")
    public List<String> getMyDevices(HttpServletRequest request) {
        Account account = accountService.findAccountByNameOrEmail(request.getUserPrincipal().getName());
        return handler.findDeviceMeidsByAccount(account);
    }

    @GetMapping("query")
    public RestBean<Location> query(HttpServletRequest request, @RequestParam String meid) {
        Account account = accountService.findAccountByNameOrEmail(request.getUserPrincipal().getName());
        if (isOwnedDevice(account, meid)) {
            return RestBean.forbidden("Not your device (otherwise the device was offline)");
        }
        Location location = handler.findLocationByMeid(meid);
        if (location == null) {
            return RestBean.failure(444, "Invalid Find Device client, are you using PostMan?");
        }
        return RestBean.success(location);
    }

    /**
     * 向客户端发送消息以强制刷新位置
     * */
    @GetMapping("force-query")
    public RestBean<String> force(HttpServletRequest request, @RequestParam String meid) throws Exception{
        Account account = accountService.findAccountByNameOrEmail(request.getUserPrincipal().getName());
        if (isOwnedDevice(account, meid)) {
            return RestBean.forbidden("Not your device (otherwise the device was offline)");
        }
        handler.sendLocateRequest(meid);
        return RestBean.success("Request has sent");
    }

    protected boolean isOwnedDevice(Account account, String meid) {
        return !handler.findDeviceMeidsByAccount(account).contains(meid);
    }
}
