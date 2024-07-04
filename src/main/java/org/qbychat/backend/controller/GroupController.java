package org.qbychat.backend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.qbychat.backend.entity.Account;
import org.qbychat.backend.entity.Group;
import org.qbychat.backend.entity.RestBean;
import org.qbychat.backend.service.impl.AccountServiceImpl;
import org.qbychat.backend.service.impl.GroupsServiceImpl;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/group")
public class GroupController {
    @Resource
    GroupsServiceImpl groupsService;
    @Resource
    private AccountServiceImpl accountService;

    @GetMapping("query")
    public RestBean<Group> query(@RequestParam String name) {
        Group group = groupsService.getGroupByName(name);
        if (group == null) {
            return RestBean.failure(404, "Group not found");
        }
        return RestBean.success(group);
    }

    @PostMapping("create")
    public RestBean<String> createGroup(@RequestParam String name, HttpServletRequest request) {
        Account account = accountService.findAccountByNameOrEmail(request.getUserPrincipal().getName());
        if (groupsService.createGroup(account, name)) {
            return RestBean.success("Group created");
        }
        return RestBean.failure(409, "Group creation failed");
    }
}
