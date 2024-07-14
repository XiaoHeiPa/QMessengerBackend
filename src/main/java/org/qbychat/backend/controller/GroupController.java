package org.qbychat.backend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
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

    @GetMapping("query/id/{id}")
    public RestBean<Group> queryById(@PathVariable("id") int id) {
        Group group = groupsService.findGroupById(id);
        if (group == null) {
            return RestBean.failure(404, "Group not found");
        }
        return RestBean.success(group);
    }

    @GetMapping("query/name/{name}")
    public RestBean<Group> query(@PathVariable String name) {
        Group group = groupsService.findGroupByName(name);
        if (group == null) {
            return RestBean.failure(404, "Group not found");
        }
        return RestBean.success(group);
    }

    @GetMapping("join")
    public RestBean<String> join(@PathParam("id") int id) {
        Group group = groupsService.findGroupById(id);
        if (group == null) return RestBean.failure(404, "Group not found");
        group.getMembers().add(id);
        groupsService.updateGroup(group);
        return RestBean.success();
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
