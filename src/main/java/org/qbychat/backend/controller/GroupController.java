package org.qbychat.backend.controller;

import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import org.qbychat.backend.entity.Account;
import org.qbychat.backend.entity.Group;
import org.qbychat.backend.entity.RestBean;
import org.qbychat.backend.entity.Role;
import org.qbychat.backend.service.impl.AccountServiceImpl;
import org.qbychat.backend.service.impl.GroupsServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupController {
    @Resource
    GroupsServiceImpl groupsService;
    @Resource
    private AccountServiceImpl accountService;

    @Value("${messenger.message.page.size}")
    private int pageSize;

    @GetMapping("query/id/{id}")
    @ApiOperation("通过查询id查询群聊")
    public RestBean<Group> queryById(@PathVariable("id") int id) {
        Group group = groupsService.findGroupById(id);
        if (group == null) {
            return RestBean.failure(404, "Group not found");
        }
        return RestBean.success(group);
    }

    @GetMapping("query/name/{name}")
    @ApiOperation("通过查询name查询群聊")
    public RestBean<Group> query(@PathVariable String name) {
        Group group = groupsService.findGroupByName(name);
        if (group == null) {
            return RestBean.failure(404, "Group not found");
        }
        return RestBean.success(group);
    }

    @GetMapping("member/{id}")
    @ApiOperation("通过id查询群聊成员")
    public RestBean<List<Account>> queryMembers(@PathVariable("id") int groupId, @RequestParam int pageSize,@RequestParam int page, HttpServletRequest request) {
        Group group = groupsService.findGroupById(groupId);
        if (group == null) {
            return RestBean.failure(404, "Group not found");
        }
        Account account = accountService.findAccountByNameOrEmail(request.getUserPrincipal().getName());
        if (!(account.getRole().equals(Role.ADMIN) || group.getMembers().contains(account.getId()))) {
            return RestBean.failure(403, "No permission");
        }
        List<Account> result = new ArrayList<>();

        List<Integer> resPage = groupsService.queryMembers(group, pageSize, page);
        for (Integer i: resPage){
            result.add(accountService.findAccountById(i));
        }

        return RestBean.success(result);
    }

    @GetMapping("join")
    @ApiOperation("加入群聊")
    public RestBean<String> join(@PathParam("id") int id) {
        Group group = groupsService.findGroupById(id);
        if (group == null) return RestBean.failure(404, "Group not found");
        group.getMembers().add(id);
        groupsService.updateGroup(group);
        return RestBean.success();
    }

    @PostMapping("create")
    @ApiOperation("创建群聊")
    public RestBean<String> createGroup(@RequestParam String name, HttpServletRequest request) {
        Account account = accountService.findAccountByNameOrEmail(request.getUserPrincipal().getName());
        if (groupsService.createGroup(account, name)) {
            return RestBean.success("Group created");
        }
        return RestBean.failure(409, "Group creation failed");
    }
}
