package org.qbychat.backend.controller;

import jakarta.annotation.Resource;
import org.qbychat.backend.entity.Group;
import org.qbychat.backend.entity.RestBean;
import org.qbychat.backend.service.impl.GroupsServiceImpl;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/group")
public class GroupController {
    @Resource
    GroupsServiceImpl groupsService;

    @GetMapping("query")
    public RestBean<Group> query(@RequestParam String name) {
        Group group = groupsService.getGroupByName(name);
        if (group == null) {
            return RestBean.failure(404, "Group not found");
        }
        return RestBean.success(group);
    }
}
